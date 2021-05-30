package com.example.demo.fileDrop;

import com.example.demo.student.SharedFile;
import com.example.demo.student.SharedFilesRepository;
import com.example.demo.student.Student;
import com.example.demo.student.StudentService;
import com.example.demo.user_service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.*;


@Service
public class FileService {

    @Autowired
    SharedFilesRepository sharedFilesRepository;

    @Autowired
    StudentService userRepository;

    private String myFolder = "C:/uploads/";

    private Student me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<Student> student = userRepository.getStudentByEmail(userDetails.getEmail());
        if (student.isPresent())
            return student.get();
        else
            throw new IllegalStateException("user not logged in???");
    }


    private boolean authPath(String path) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return path.substring(0, userDetails.getUsername().length() + 1).equals(userDetails.getUsername() + "/");
    }

    private boolean fileExists(String path) {
        if (authPath(path)) {
            path = myFolder + path;
            File file = new File(path);
            return file.exists();
        }
        return false;
    }

    public String uploadFilesToDir(MultipartFile[] files, String dir) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        for (MultipartFile file : files) {
            try {
                String uploadDir = "C:/uploads/" + userDetails.getUsername() + "/" + dir;
                String originalFileName = URLDecoder.decode(file.getOriginalFilename(), "UTF-8"); //do polskich znaków
                File transferFile = new File(uploadDir + "/" + originalFileName);

                //make dir if not exist for the file to store it
                if (!transferFile.exists()) {
                    transferFile.mkdirs();
                }
                file.transferTo(transferFile);

            } catch (Exception e) {
                e.printStackTrace();
                return "Something went wrong";
            }
        }

        return "Success";
    }

    public FileNames getAllFilenames() {
        return new FileNames(me());
    }

    public File getFileFor(String path) throws FileNotFoundException {
        File file = new File(myFolder + path);
        if (!file.exists())
            throw new FileNotFoundException();

        if (authPath(path)) {
            return file;
        } else {
            SharedFile sharedFile = sharedFilesRepository.findSharedFileByPathOrError(path);
            if (sharedFile.getSharedStudent().contains(me()))
                return file;
            else
                throw new IllegalArgumentException();
        }
    }

    public boolean deleteFile(String path) throws FileNotFoundException {
        if (authPath(path)) {
            File file = new File(myFolder + path);
            if (!file.exists())
                throw new FileNotFoundException();
            else {
                if (file.delete()) {
                    Optional<SharedFile> sharedFile = sharedFilesRepository.findSharedFileByPath(path);
                    if (sharedFile.isPresent()) {
                        SharedFile realFile = sharedFile.get();
                        realFile.delete();
                        sharedFilesRepository.delete(realFile);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean renameFile(String path, String name) throws FileNotFoundException {
        if(authPath(path)){
            File file = new File(myFolder + path);
            if (!file.exists())
                throw new FileNotFoundException();
            String newPath = path.substring(0, path.lastIndexOf('/')) + path.substring(path.lastIndexOf('/') + 1);
            File file2 = new File(myFolder + newPath);
            if(file2.exists()){
                return false;
            }
            if(file.renameTo(file2)){
                SharedFile sharedFile = sharedFilesRepository.findSharedFileByPath(path).orElse(null);
                if(sharedFile != null){
                    sharedFile.setFilePath(newPath);
                    sharedFilesRepository.save(sharedFile);
                }
                return true;
            }
        }
        return false;
    }

    public void unShareFile(String filePath, String email) throws EntityNotFoundException {
        SharedFile file = sharedFilesRepository.findSharedFileByPathOrError(filePath);

        if (email.equals("")) {
            if (file.getOwner().equals(me().getEmail())) {
                file.delete();
                sharedFilesRepository.delete(file);
            } else {
                if (me().getShared().contains(file)) {
                    me().removeSharedFile(file);
                    userRepository.save(me());
                }
            }
        } else {
            if (file.getOwner().equals(me().getEmail())) {
                Optional<Student> user = userRepository.getStudentByEmail(email);
                if (user.isPresent()) {
                    user.get().removeSharedFile(file);
                    userRepository.save(user.get());
                } else
                    throw new EntityNotFoundException();
            }
        }

        if (file.getSharedStudent().size() == 1) {
            me().removeSharedFile(file);
            sharedFilesRepository.delete(file);
        }
    }

    public void shareFile(String filePath, String email) throws FileNotFoundException {
        Optional<Student> optionalShareUser = userRepository.getStudentByEmail(email);
        if (optionalShareUser.isEmpty())
            throw new EntityNotFoundException();
        if (fileExists(filePath))
            userRepository.shareFile(optionalShareUser.get(), filePath);
        else
            throw new FileNotFoundException();
    }

    public Set getSharedWith(String filePath) {
        if (fileExists(filePath)) {
            return sharedFilesRepository.findSharedFileByPathOrError(filePath).getSharedStudent();
        } else
            throw new NoSuchElementException();
    }

    public Set<SharedFile> sharedFiles() {
        Set<SharedFile> sharedFiles = me().getShared();
        Set<SharedFile> shared = new HashSet<>();
        for (SharedFile file : sharedFiles) {
            if (file.getOwner().equals(me().getEmail()))
                shared.add(file);
        }
        return shared;
    }
}

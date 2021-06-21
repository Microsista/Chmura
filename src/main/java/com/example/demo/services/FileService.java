package com.example.demo.services;

import com.example.demo.DTOs.FileNames;
import com.example.demo.models.ImageLocation;
import com.example.demo.DTOs.Image;
import com.example.demo.models.SharedFile;
import com.example.demo.repositories.SharedFilesRepository;
import com.example.demo.models.Student;
import com.example.demo.models.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;


@Service
public class FileService {

    @Autowired
    ImageLocationService imageService;

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

    private String getUploadPath(String relativePath) {
        return myFolder + me().getUsername() + "/" + relativePath;
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

    public String uploadImage(Image image) {
        String path = getUploadPath(image.getPath());
        File transferFile = new File(path);
        if (!transferFile.exists()) {
            byte[] imageByte = Base64.getDecoder().decode(image.getImage());
            try {
                File makeDir = new File(path.substring(0, path.lastIndexOf('/')));
                makeDir.mkdirs();
                new FileOutputStream(path).write(imageByte);
                ImageLocation imageLocation = new ImageLocation(me().getUsername() + "/" + image.getPath(), image.getGeoHeight(), image.getGeoWidth());
                imageService.save(imageLocation);
                return "success";
            } catch (IOException e) {
                return "Sth went wrong";
            }
        } else
            return "file already exists";
    }

    public String getGeoLocation(String path) throws FileNotFoundException {
        ImageLocation image = imageService.findPictureByPath(path).orElseThrow(FileNotFoundException::new);
        return image.getHeightAndWidth();
    }

    public String uploadFilesToDir(MultipartFile[] files, String dir) {

        for (MultipartFile file : files) {
            try {
                String uploadDir = getUploadPath(dir);
                String originalFileName = URLDecoder.decode(file.getOriginalFilename(), "UTF-8"); //do polskich znaków
                File transferFile = new File(uploadDir + "/" + originalFileName);

                //make dir if not exist for the file to store it
                if (!transferFile.exists()) transferFile.mkdirs();
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
                    imageService.deleteWithPath(path);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean renameFile(String path, String name) throws FileNotFoundException {
        if (authPath(path)) {
            System.out.println("file 1: " + myFolder + path);
            File file = new File(myFolder + path);
            if (!file.exists())
                throw new FileNotFoundException();
            String newPath = path.substring(0, path.lastIndexOf('/') + 1) + name;
            System.out.println("file new: " + myFolder + newPath);
            File file2 = new File(myFolder + newPath);
            if (file2.exists()) {
                return false;
            }
            if (file.renameTo(file2)) {
                SharedFile sharedFile = sharedFilesRepository.findSharedFileByPath(path).orElse(null);
                if (sharedFile != null) {
                    sharedFile.setFilePath(newPath);
                    sharedFilesRepository.save(sharedFile);
                }
                imageService.rename(path, newPath);
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

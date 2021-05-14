package com.example.demo.fileDrop;

import com.example.demo.user_service.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Service
public class FileService {

    @Autowired
    ObjectMapper mapper;

    private String myFolder = "C:/uploads/";

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

    private void getFileNames(HashMap<String, List<String>> map, File startDir, String PathName) {
        File[] dirs = startDir.listFiles();
        List<String> fileNames = new ArrayList<>();
        assert dirs != null;
        for (File current : dirs) {
            if (current.isDirectory()) {
                if (PathName != null)
                    getFileNames(map, current, PathName + "/" + startDir.getName());
                else
                    getFileNames(map, current, startDir.getName());
            } else {
                fileNames.add(current.getName());
            }
        }
        if (PathName != null)
            map.put(PathName + "/" + startDir.getName(), fileNames);
        else
            map.put(startDir.getName(), fileNames);
    }

    public HashMap getAllFilenames() {
        HashMap<String, List<String>> map = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String userFolder = "C:/uploads/" + userDetails.getUsername();
        File startDir;
        try {
            startDir = new File(userFolder);
            getFileNames(map, startDir, null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return map;
    }

    private boolean authPath(String path) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return path.substring(0, userDetails.getUsername().length() + 1).equals(userDetails.getUsername() + "/");
    }

    public File getFileFor(String path) throws FileNotFoundException {
        File file;
        if (authPath(path)) {
            file = new File(myFolder + path);
            if (!file.exists())
                throw new FileNotFoundException();
        } else {
            throw new IllegalArgumentException();
        }
        return file;
    }

    public boolean deleteFile(String path) throws FileNotFoundException {
        if (authPath(path)) {
            File file = new File(myFolder + path);
            if (!file.exists())
                throw new FileNotFoundException();
            else {
                return file.delete();
            }
        }
        return false;
    }
}

package com.example.demo.DTOs;

import com.example.demo.models.SharedFile;
import com.example.demo.models.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileNames {
    public HashMap<String, List<String>> myFiles = new HashMap<>();
    public HashMap<String, List<String>> sharedFiles = new HashMap<>();

    public FileNames(Student me) {
        String userFolder = "C:/uploads/" + me.getUsername();
        File startDir;
        try {
            startDir = new File(userFolder);
            getFileNames(myFiles, startDir, null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (SharedFile f : me.getShared()) {
            if (!f.getOwner().equals(me.getEmail())) {
                String path = f.getFilePath().substring(0, f.getFilePath().lastIndexOf('/'));
                String fileName = f.getFilePath().substring(path.length() + 1);
                List<String> list;
                if (sharedFiles.containsKey(path))
                    list = sharedFiles.get(path);
                else
                    list = new ArrayList<>();
                list.add(fileName);
                sharedFiles.put(path, list);
            }
        }
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
}

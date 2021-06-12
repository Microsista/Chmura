package com.example.demo.controllers;

import com.example.demo.fileDrop.FileNames;
import com.example.demo.DTOs.Image;
import com.example.demo.services.FileService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


import javax.persistence.EntityNotFoundException;
import java.io.FileNotFoundException;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping
    public String uploadFile(@RequestParam("files") MultipartFile[] files, @RequestParam(required = false, defaultValue = "") String dir) {
        return fileService.uploadFilesToDir(files, dir);
    }

    @GetMapping
    public FileNames getFileNamesAndDirs() {
        return fileService.getAllFilenames();
    }

    @PostMapping("/image")
    public String uploadImage(@RequestBody Image image) {
        return fileService.uploadImage(image);
    }


    @GetMapping("/image")
    public ResponseEntity<?> getGeoLocation(@RequestParam("file_path") String filePath) {
        try {
            return ResponseEntity.ok(fileService.getGeoLocation(filePath));
        } catch (FileNotFoundException e) {
            return ResponseEntity.ok("0, 0, 0, 0"); //standard geoloc
        }
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<?> getFile(@RequestParam("file_path") String filePath) {
        try {
            return ResponseEntity.ok(new FileSystemResource(fileService.getFileFor(filePath)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body("próba dostania się do nie swoich plików");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("nie znaleziono pliku na serwerze");
        }
    }

    @DeleteMapping(path = "/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFile(@RequestParam("file_path") String filePath) {
        try {
            if (fileService.deleteFile(filePath)) {
                return ResponseEntity.ok("plik został usunięty");
            } else
                return ResponseEntity.status(400).body("coś poszło nie tak");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("nie znaleziono pliku na serwerze");
        }
    }

    @PostMapping(path = "/share")
    public ResponseEntity<?> shareFile(@RequestParam("file_path") String filePath, @RequestParam("email") String email) {
        try {
            fileService.shareFile(filePath, email);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("file not found");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("user not found");
        }
        return ResponseEntity.ok("file shared");
    }

    //who are files shared to
    @GetMapping("/sharedWith")
    public ResponseEntity<?> shareFilesWith(@RequestParam("file_path") String filePath) {
        try {
            return ResponseEntity.ok(fileService.getSharedWith(filePath));
        } catch (EntityNotFoundException | NoSuchElementException e) {
            return ResponseEntity.status(404).body("file not found");
        }
    }

    //unshare shared file, with mail unshare with that user, without unshare with all users if owner or unshare with owner
    @DeleteMapping("/unshare")
    public ResponseEntity<?> unShareFile(@RequestParam("file_path") String filePath, @RequestParam(name = "email", required = false, defaultValue = "") String email) {
        try {
            fileService.unShareFile(filePath, email);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body("file or user not found");
        }
        return ResponseEntity.ok("file unshared");
    }

    @PutMapping("/rename")
    public ResponseEntity<?> renameFile(@RequestParam("file_path") String filePath, @RequestParam("name") String name) {
        try {
            if (fileService.renameFile(filePath, name))
                return ResponseEntity.ok("file renamed");
            else
                return ResponseEntity.status(404).body("file with that name already exists or not authorized");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("file not found");
        }
    }

    //files shared by me
    @GetMapping("/shared")
    public ResponseEntity<?> sharedFiles() {
        return ResponseEntity.ok(fileService.sharedFiles());
    }
}

package com.example.demo.fileDrop;

import com.example.demo.login.JwtResponse;
import com.example.demo.login.JwtUtils;
import com.example.demo.login.LoginForm;
import com.example.demo.student.Student;
import com.example.demo.student.StudentService;
import com.example.demo.user_service.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.util.HashMap;


@RestController
@CrossOrigin
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    StudentService userRepository;

    @Autowired
    ServletContext context;

    @PostMapping
    public String uploadFile(@RequestParam("files") MultipartFile[] files, @RequestParam(required = false, defaultValue = "") String dir) {
        return fileService.uploadFilesToDir(files, dir);
    }

    @GetMapping
    public HashMap getFileNamesAndDirs() {
        return fileService.getAllFilenames();
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

    @GetMapping(path = "/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFile(@RequestParam("file_path") String filePath) {
        try {
            if (fileService.deleteFile(filePath))
                return ResponseEntity.ok("plik został usunięty");
            else
                return ResponseEntity.status(400).body("coś poszło nie tak");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("nie znaleziono pliku na serwerze");
        }
    }
}

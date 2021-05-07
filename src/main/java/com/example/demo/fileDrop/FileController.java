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
import java.util.HashMap;


@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    StudentService userRepository;

    @Autowired
    ServletContext context;

    @PostMapping
    public String uploadFile(@RequestParam("files") MultipartFile[] files, @RequestParam("dir") String dir) {
        return fileService.uploadFilesToDir(files, dir);
    }

    @GetMapping
    public HashMap getFileNamesAndDirs(){
        return fileService.getAllFilenames();
    }
}

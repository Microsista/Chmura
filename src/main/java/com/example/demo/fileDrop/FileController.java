package com.example.demo.fileDrop;

import com.example.demo.login.JwtResponse;
import com.example.demo.login.JwtUtils;
import com.example.demo.login.LoginForm;
import com.example.demo.student.SharedFile;
import com.example.demo.student.SharedFilesRepository;
import com.example.demo.student.Student;
import com.example.demo.student.StudentService;
import com.example.demo.user_service.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;


@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    SharedFilesRepository sharedFilesRepository;

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
            if (fileService.deleteFile(filePath)) {
                Optional<SharedFile> file = sharedFilesRepository.findSharedFileByPath(filePath);
                if (file.isPresent()) {
                    SharedFile realFile = file.get();
                    realFile.delete();
                    sharedFilesRepository.delete(realFile);
                }
                return ResponseEntity.ok("plik został usunięty");
            } else
                return ResponseEntity.status(400).body("coś poszło nie tak");
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(404).body("nie znaleziono pliku na serwerze");
        }
    }

    @PostMapping(path = "/share")
    public ResponseEntity<?> shareFile(@RequestParam("file_path") String filePath, @RequestParam("email") String email) {
        System.out.println(filePath);
        Optional<Student> optionalShareUser = userRepository.getStudentByEmail(email);
        if (optionalShareUser.isEmpty())
            return ResponseEntity.status(404).body("nie znaleziono użytkownika o tym emailu");

        if (fileService.fileExists(filePath)) {
            userRepository.shareFile(optionalShareUser.get(), filePath);
            return ResponseEntity.ok("plik udostępniony");
        }
        return ResponseEntity.status(404).body("nie można udostępnić pliku");
    }

    @GetMapping("/shared")
    public ResponseEntity<?> shareFiles() throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<Student> student = userRepository.getStudentByEmail(userDetails.getEmail());

        if (student.isPresent()) {
            Student me = student.get();
            Set<SharedFile> test = me.getShared();
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println(objectMapper.writeValueAsString(test));
            return ResponseEntity.ok(test);
        }
        return ResponseEntity.status(500).body("sth is really wrong m8");
    }
}

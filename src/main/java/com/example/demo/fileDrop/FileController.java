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
@CrossOrigin
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    SharedFilesRepository sharedFilesRepository;

    @Autowired
    StudentService userRepository;

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
        Optional<Student> optionalShareUser = userRepository.getStudentByEmail(email);
        if (optionalShareUser.isEmpty())
            return ResponseEntity.status(404).body("nie znaleziono użytkownika o tym emailu");
        if (fileService.fileExists(filePath)) {
            userRepository.shareFile(optionalShareUser.get(), filePath);
            return ResponseEntity.ok("plik udostępniony");
        }
        return ResponseEntity.status(404).body("nie można udostępnić pliku");
    }

    @GetMapping("/sharedWith")
    public ResponseEntity<?> shareFilesWith(@RequestParam("file_path") String filePath) {
        Student me = me();
        if (fileService.fileExists(filePath)) {
            Optional<SharedFile> sharedFile = sharedFilesRepository.findSharedFileByPath(filePath);
            if (sharedFile.isPresent()) {
                return ResponseEntity.ok(sharedFile.get().sharedWith());
            }
        }
        return ResponseEntity.status(404).body("file not found or unauthorized");
    }

    @DeleteMapping("/unshare")
    public ResponseEntity<?> unShareFiles(@RequestParam("file_path") String filePath, @RequestParam(name = "email", required = false, defaultValue = "") String email) {
        Student me = me();
        Optional<SharedFile> file = sharedFilesRepository.findSharedFileByPath(filePath);
        SharedFile realFile;
        if (file.isPresent())
            realFile = file.get();
        else
            return ResponseEntity.status(404).body("file not found");
        if (!realFile.getOwner().equals(me.getEmail()))
            return ResponseEntity.status(401).body("not yours file");

        if (!email.equals("")) {
            Optional<Student> user = userRepository.getStudentByEmail(email);
            if (user.isPresent()) {
                user.get().deleteSharedFile(realFile);
                userRepository.save(user.get());
            } else
                return ResponseEntity.status(404).body("user not found");
        } else {
            realFile.delete();
            sharedFilesRepository.delete(realFile);
        }
        return ResponseEntity.ok("file unshared");
    }

    @GetMapping("/shared")
    public ResponseEntity<?> shareFiles() throws JsonProcessingException {
        Student me = me();
        Set<SharedFile> test = me.getShared();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(test));
        return ResponseEntity.ok(test);
    }

    private Student me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<Student> student = userRepository.getStudentByEmail(userDetails.getEmail());
        if (student.isPresent())
            return student.get();
        else
            throw new IllegalStateException("user not logged in???");
    }
}

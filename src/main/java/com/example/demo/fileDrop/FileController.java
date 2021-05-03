package com.example.demo.fileDrop;

import com.example.demo.login.JwtResponse;
import com.example.demo.login.JwtUtils;
import com.example.demo.login.LoginForm;
import com.example.demo.student.Student;
import com.example.demo.student.StudentService;
import com.example.demo.user_service.UserDetailsImpl;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(path = "api/fileDrop")
public class FileController {

    @Autowired
    StudentService userRepository;

    @Autowired
    ServletContext context;

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            String uploadDir = "C:/uploads/";
            String originalFileName = URLDecoder.decode(file.getOriginalFilename(), "UTF-8"); //do polskich znaków
            System.out.println(originalFileName);
            File transferFile = new File(uploadDir + "/" + originalFileName);

            //make dir if not exist for the file to store it
            if (!transferFile.exists()) {
                transferFile.mkdirs();
            }
            file.transferTo(transferFile);

        } catch (Exception e) {
            e.printStackTrace();
            return "Failure";
        }

        return "Success";
    }

    @GetMapping
    public String setCookieTest(HttpServletResponse response) {
        //TODO: later make it work lol, cookies will be useful

        // create a cookie with test token
        String uploadDir = "/uploads/";
        String realPath = context.getRealPath(uploadDir);
        System.out.println(realPath);

        Cookie cookie = new Cookie("token", "");

        //add cookie to response
        response.addCookie(cookie);
        return "Username is changed!";
    }
}

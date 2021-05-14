package com.example.demo.login;

import com.example.demo.student.Student;
import com.example.demo.student.StudentService;
import com.example.demo.user_service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping(path = "api/auth")
public class LoginController {

    @Autowired
    StudentService userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/signInTest")
    public ResponseEntity<?> signInTest(HttpServletResponse response) throws Exception {
        //test token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("Mariam", "123"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);

        // create a cookie with test token
        Cookie cookie = new Cookie("token", token);

        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days - to delete cookie set this to 0
        cookie.setPath("/"); // global cookie accessible every where

        //add cookie to response
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged in");
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginForm loginRequest, HttpServletResponse response) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.generateJwtToken(authentication);

        // create a cookie with test token
        Cookie cookie = new Cookie("token", token);

        cookie.setMaxAge(7 * 24 * 60 * 60); // expires in 7 days - to delete cookie set this to 0
        cookie.setPath("/"); // global cookie accessible every where

        //add cookie to response
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged in");
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        // Create new user's account
        Student student = new Student(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), signUpRequest.getDob());

        try {
            userRepository.addNewStudent(student);
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.toString());
        }

        return ResponseEntity.ok("User registered successfully!");
    }


    @GetMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        userRepository.deleteStudent(userDetails.getId());

        return ResponseEntity.ok("User deleted successfully!");
    }


    @GetMapping("/logOut")
    public ResponseEntity<?> logOut(HttpServletResponse response) {
        // create a cookie with test token
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0); //to delete cookie set this to 0
        cookie.setPath("/"); // global cookie accessible every where - needed to delete

        //add cookie to response to destroy cookie on logout
        response.addCookie(cookie);
        return ResponseEntity.ok("User logged out!");
    }
}

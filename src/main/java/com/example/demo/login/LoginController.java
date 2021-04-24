package com.example.demo.login;

import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "api/login")
public class LoginController {
    private final StudentRepository studentRepository;

    @Autowired
    public LoginController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody User loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername()));
    }

    @PostMapping()
    public ResponseEntity postController(@RequestBody User user) {

        Optional<Student> studentOptional = studentRepository.findStudentByEmail(user.getUsername());
        if (studentOptional.isPresent()) {
            if (user.getPassword().equals(studentOptional.get().getPassword())) {
                return ResponseEntity.ok(HttpStatus.ACCEPTED);
            }
        }

        //bad password
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }
}

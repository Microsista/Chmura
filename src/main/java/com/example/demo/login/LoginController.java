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


import javax.validation.Valid;
import java.time.LocalDate;


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


    @PostMapping("/signIn")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginForm loginRequest) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        LocalDate dob = LocalDate.now();

        // Create new user's account
        Student student = new Student(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()), dob);

        try {
            userRepository.addNewStudent(student);
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.toString());
        }

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        userRepository.deleteStudent(userDetails.getId());

        return ResponseEntity.ok("User deleted successfully!");
    }
}

package com.example.demo.login;

import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
@RequestMapping(path = "api/login")
public class LoginController {
    private final StudentRepository studentRepository;

    @Autowired
    public LoginController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
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

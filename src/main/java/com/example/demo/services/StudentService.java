package com.example.demo.services;

import com.example.demo.models.UserDetailsImpl;
import com.example.demo.models.SharedFile;
import com.example.demo.repositories.SharedFilesRepository;
import com.example.demo.models.Student;
import com.example.demo.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SharedFilesRepository sharedFilesRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, SharedFilesRepository sharedFilesRepository) {
        this.studentRepository = studentRepository;
        this.sharedFilesRepository = sharedFilesRepository;
    }

    public void save(Student s) {
        studentRepository.save(s);
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void shareFile(Student student, String filePath) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<Student> optional = studentRepository.findStudentByUsername(userDetails.getUsername());
        if (optional.isEmpty())
            throw new IllegalStateException("Logged user doesn't exist??");
        Student me = optional.get();
        Optional<SharedFile> sharedFile = sharedFilesRepository.findSharedFileByPath(filePath);
        SharedFile file;
        if (sharedFile.isEmpty()) {
            file = new SharedFile(filePath, me.getEmail());
            me.addSharedFile(file);
        } else {
            file = sharedFile.get();
        }

        student.addSharedFile(file);
        sharedFilesRepository.save(file);
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentRepository
                .findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }

        studentOptional = studentRepository
                .findStudentByUsername(student.getUsername());
        if (studentOptional.isPresent()) {
            throw new IllegalStateException("username taken");
        }

        studentRepository.save(student);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findStudentByEmail(email);
    }

    public void deleteStudent(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (!exists) {
            throw new IllegalStateException(
                    "student with id " + studentId + " does not exists");
        }
        studentRepository.deleteById(studentId);
    }

    // This annotation means you do not have to implement any JPQL(from repository) queries.
    // You can use setters from query, to check whether you can or cannot update
    // and use setters to automatically update the entity in your database.
    @Transactional
    public void updateStudent(Long studentId,
                              String username,
                              String email) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException(
                "student with id " + studentId + " does not exist."));

        if (username != null &&
                username.length() > 0 &&
                !Objects.equals(student.getUsername(), username)) {
            student.setUsername(username);
        }

        if (email != null &&
                email.length() > 0 &&
                !Objects.equals(student.getEmail(), email)) {
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
            if (studentOptional.isPresent()) {
                throw new IllegalStateException("email taken");
            }
            student.setEmail(email);
        }
    }
}

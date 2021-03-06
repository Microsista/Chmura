package com.example.demo.configs;

import com.example.demo.models.Student;
import com.example.demo.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.*;

@Configuration
public class StudentConfig {

    @Autowired
    PasswordEncoder encoder;

    @Bean
    CommandLineRunner commandLineRunner(
            StudentRepository repository) {
        return args -> {
            Student mariam = new Student(
                    "Mariam",
                    "test@gmail.com",
                    encoder.encode("123"),
                    LocalDate.of(2000, JANUARY, 5)
            );

            Student alex = new Student(
                    "Alex",
                    "alex@gmail.com",
                    encoder.encode("alexpass"),
                    LocalDate.of(2004, JANUARY, 5)
            );

            repository.saveAll(
                    List.of(mariam, alex)
            );
        };
    }
}

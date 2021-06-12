package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Student {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_sequence"
    )
    @JsonIgnore
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private LocalDate dob;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<SharedFile> shared = new HashSet<>();

    // Don't store it in the database, mark it as being derived from other fields.
    @Transient
    @JsonIgnore
    private Integer age;

    public Student() {
    }

    public Set<SharedFile> getShared() {
        return shared;
    }

    public Student(
            String username,
            String email,
            String password, LocalDate dob) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dob = dob;
    }

    public void addSharedFile(SharedFile file) {
        file.addUserToShare(this);
        shared.add(file);
    }

    public void removeSharedFile(SharedFile file) {
        shared.remove(file);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return Period.between(this.dob, LocalDate.now()).getYears();
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", dob=" + dob +
                ", age=" + age +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.example.demo.student;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class SharedFile {
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String filePath;
    private String owner;

    @JsonBackReference
    @ManyToMany(mappedBy = "shared")
    private Set<Student> sharedStudent = new HashSet<>();

    public SharedFile(String filePath, String owner) {
        this.filePath = filePath;
        this.owner = owner;
    }

    public SharedFile() {
    }

    public Set<Student> getSharedStudent() {
        return sharedStudent;
    }

    public void addUserToShare(Student user) {
        sharedStudent.add(user);
    }

    public String getFilePath() {
        return filePath;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "SharedFile{" +
                "owner=" + owner +
                ", file=" + filePath +
                '}';
    }

    public void delete() {
        for (Student s :
                sharedStudent) {
            s.deleteSharedFile(this);
        }
    }
}

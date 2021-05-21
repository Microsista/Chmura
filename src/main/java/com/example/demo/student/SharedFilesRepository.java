package com.example.demo.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedFilesRepository
        extends JpaRepository<SharedFile, Long> {

    @Query("SELECT s FROM SharedFile s WHERE s.filePath = ?1")
    Optional<SharedFile> findSharedFileByPath(String path);
}
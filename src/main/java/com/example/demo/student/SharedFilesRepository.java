package com.example.demo.student;

import org.apache.catalina.connector.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Repository
public interface SharedFilesRepository
        extends JpaRepository<SharedFile, Long> {

    @Query("SELECT s FROM SharedFile s WHERE s.filePath = ?1")
    Optional<SharedFile> findSharedFileByPath(String path);

    default SharedFile findSharedFileByPathOrError(String path) {
        return findSharedFileByPath(path).orElseThrow(EntityNotFoundException::new);
    }
}
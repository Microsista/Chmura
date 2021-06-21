package com.example.demo.repositories;

import com.example.demo.models.ImageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<ImageLocation, Long> {

    @Query("SELECT s FROM ImageLocation s WHERE s.filePath = ?1")
    Optional<ImageLocation> findPictureByPath(String path);
}

package com.example.demo.services;

import com.example.demo.fileDrop.ImageLocation;
import com.example.demo.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.util.Optional;

@Service
public class ImageLocationService {

    @Autowired
    ImageRepository imageRepository;

    public void save(ImageLocation imageLocation) {
        imageRepository.save(imageLocation);
    }

    public Optional<ImageLocation> findPictureByPath(String path) {
        return imageRepository.findPictureByPath(path);
    }

    public ImageLocation getImageLocation(String path) throws FileNotFoundException {
        return findPictureByPath(path).orElseThrow(FileNotFoundException::new);
    }

    @Transactional
    public void rename(String old_path, String new_path) {
        try {
            ImageLocation imageLocation = getImageLocation(old_path);
            imageLocation.setFilePath(new_path);
        } catch (FileNotFoundException ignored) {
        }
    }

    public void deleteWithPath(String path) {
        try {
            ImageLocation imageLocation = getImageLocation(path);
            imageRepository.delete(imageLocation);
        } catch (FileNotFoundException ignored) {

        }
    }
}

package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.ImageUploadResponse;
import com.rideshare.userservice.exception.InvalidFileException;
import com.rideshare.userservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${app.upload.profile-picture-path}")
    private String uploadPath;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Override
    public ImageUploadResponse uploadProfilePicture(MultipartFile image) {

        validateImage(image);

        try {

            Path directory = Paths.get(uploadPath);

            System.out.println("Upload Directory: " + directory.toAbsolutePath());

            Files.createDirectories(directory);

            String extension = getFileExtension(image.getOriginalFilename());

            String fileName = UUID.randomUUID() + extension;

            Path target = directory.resolve(fileName);

            System.out.println("Saving File To: " + target.toAbsolutePath());

            Files.copy(
                    image.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("File Exists: " + Files.exists(target));

            String imageUrl =
                    "/uploads/profile-pictures/" + fileName;

            return new ImageUploadResponse(imageUrl);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to upload profile picture.", ex);
        }
    }

    private void validateImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new InvalidFileException("Image file is required.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(image.getContentType())) {
            throw new InvalidFileException("Only JPG, JPEG, PNG and WEBP images are allowed.");
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("Image size must not exceed 5 MB.");
        }
    }

    private String getFileExtension(String fileName) {

        String extension = StringUtils.getFilenameExtension(fileName);

        if (extension == null) {
            throw new InvalidFileException("Invalid file.");
        }

        return "." + extension;
    }
}
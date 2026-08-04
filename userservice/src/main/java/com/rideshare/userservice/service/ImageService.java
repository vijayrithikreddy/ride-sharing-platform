package com.rideshare.userservice.service;

import com.rideshare.userservice.dto.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageUploadResponse uploadProfilePicture(MultipartFile file);

}
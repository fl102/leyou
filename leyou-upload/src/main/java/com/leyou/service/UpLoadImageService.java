package com.leyou.service;

import org.springframework.web.multipart.MultipartFile;

public interface UpLoadImageService {
    String uploadImage(MultipartFile file);
}

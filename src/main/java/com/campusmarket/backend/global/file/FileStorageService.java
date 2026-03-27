package com.campusmarket.backend.global.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    FileUploadResult upload(MultipartFile file, String dirName);

    FileUploadResult uploadBytes(byte[] bytes, String dirName, String fileName, String contentType);

    byte[] download(String fileUrl);

    void deleteByUrl(String fileUrl);
}
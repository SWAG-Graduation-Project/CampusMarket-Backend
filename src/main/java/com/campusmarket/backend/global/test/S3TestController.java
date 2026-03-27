package com.campusmarket.backend.global.test;

import com.campusmarket.backend.global.file.FileStorageService;
import com.campusmarket.backend.global.file.FileUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/s3")
public class S3TestController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public FileUploadResult uploadTestFile(
            @RequestParam("file") MultipartFile file
    ) {
        return fileStorageService.upload(file, "test");
    }
}
package com.campusmarket.backend.global.file.controller;

import com.campusmarket.backend.global.ApiResponse;
import com.campusmarket.backend.global.file.FileStorageService;
import com.campusmarket.backend.global.file.FileUploadResDto;
import com.campusmarket.backend.global.file.FileUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileUploadController implements FileUploadControllerDocs {

    private final FileStorageService fileStorageService;

    // 프로필 이미지 S3 업로드 → URL 반환
    @Override
    @PostMapping(value = "/upload/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResDto> uploadProfileImage(
            @RequestPart("file") MultipartFile file
    ) {
        FileUploadResult result = fileStorageService.upload(file, "profiles");
        return ApiResponse.success(FileUploadResDto.of(result.fileUrl()));
    }

}

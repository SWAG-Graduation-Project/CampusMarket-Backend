package com.campusmarket.backend.global.file.controller;

import com.campusmarket.backend.global.ApiResponse;
import com.campusmarket.backend.global.file.FileUploadResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File", description = "파일 업로드 API")
public interface FileUploadControllerDocs {

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 사진을 S3에 업로드하고 URL을 반환합니다.")
    ApiResponse<FileUploadResDto> uploadProfileImage(
            @Parameter(description = "이미지 파일", required = true) MultipartFile file
    );

}

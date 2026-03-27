package com.campusmarket.backend.global.file;

public record FileUploadResult(
        String objectKey,
        String fileUrl,
        String originalFileName,
        String contentType,
        long size
) {
}
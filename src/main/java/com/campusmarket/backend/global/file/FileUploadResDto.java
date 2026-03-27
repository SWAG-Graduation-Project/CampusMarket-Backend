package com.campusmarket.backend.global.file;

public record FileUploadResDto(String imageUrl) {
    public static FileUploadResDto of(String imageUrl) {
        return new FileUploadResDto(imageUrl);
    }
}

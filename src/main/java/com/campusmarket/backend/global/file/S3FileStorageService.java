package com.campusmarket.backend.global.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public FileUploadResult upload(MultipartFile file, String dirName) {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        String extension = extractExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        String objectKey = dirName + "/" + storedFileName;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            return new FileUploadResult(
                    objectKey,
                    buildFileUrl(objectKey),
                    originalFileName,
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
        }
    }

    @Override
    public FileUploadResult uploadBytes(byte[] bytes, String dirName, String fileName, String contentType) {
        String objectKey = dirName + "/" + UUID.randomUUID() + "_" + fileName;

        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(bytes)
        );

        return new FileUploadResult(
                objectKey,
                buildFileUrl(objectKey),
                fileName,
                contentType,
                bytes.length
        );
    }

    @Override
    public byte[] download(String fileUrl) {
        String objectKey = extractObjectKeyFromUrl(fileUrl);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        return objectBytes.asByteArray();
    }

    @Override
    public void deleteByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String objectKey = extractObjectKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    private String buildFileUrl(String objectKey) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;
    }

    private String extractObjectKeyFromUrl(String fileUrl) {
        URI uri = URI.create(fileUrl);
        String path = uri.getPath();

        if (path.startsWith("/")) {
            return path.substring(1);
        }

        return path;
    }
}
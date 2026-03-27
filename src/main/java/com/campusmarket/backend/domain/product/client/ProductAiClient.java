package com.campusmarket.backend.domain.product.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductAiClient {

    private final WebClient productAiWebClient;

    @Value("${ai.api.secret-key:}")
    private String apiSecretKey;

    public ProductAiAnalyzeResponse analyzeProduct(List<ImagePayload> images) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        for (ImagePayload image : images) {
            builder.part("files", new ByteArrayResource(image.bytes()) {
                        @Override
                        public String getFilename() {
                            return image.fileName();
                        }
                    })
                    .contentType(MediaType.parseMediaType(image.contentType()));
        }

        try {
            return productAiWebClient.post()
                    .uri("/api/v1/images/analyze-product")
                    .headers(this::applyApiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(ProductAiAnalyzeResponse.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException exception) {
            System.out.println("🔥 AI analyze status: " + exception.getStatusCode());
            System.out.println("🔥 AI analyze response body: " + exception.getResponseBodyAsString());
            throw exception;
        }
    }

    public ProductAiBackgroundRemovalResponse removeBackground(List<ImagePayload> images) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        for (ImagePayload image : images) {
            builder.part("files", new ByteArrayResource(image.bytes()) {
                        @Override
                        public String getFilename() {
                            return image.fileName();
                        }
                    })
                    .contentType(MediaType.parseMediaType(image.contentType()));
        }

        try {
            return productAiWebClient.post()
                    .uri("/api/v1/images/remove-background")
                    .headers(this::applyApiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(ProductAiBackgroundRemovalResponse.class)
                    .block();
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException exception) {
            System.out.println("🔥 AI remove-background status: " + exception.getStatusCode());
            System.out.println("🔥 AI remove-background response body: " + exception.getResponseBodyAsString());
            throw exception;
        }
    }

    private void applyApiKey(HttpHeaders headers) {
        if (StringUtils.hasText(apiSecretKey)) {
            headers.set("x-api-key", apiSecretKey);
        }
    }

    public record ImagePayload(
            byte[] bytes,
            String fileName,
            String contentType
    ) {
    }

    public record ProductAiAnalyzeResponse(
            String major,
            String sub_category,
            String product_name,
            String color,
            String condition,
            String description
    ) {
    }

    public record ProductAiBackgroundRemovalResponse(
            List<String> images,
            Integer count
    ) {
    }
}
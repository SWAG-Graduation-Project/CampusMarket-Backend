package com.campusmarket.backend.domain.member.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimetableAiClient {

    private final WebClient productAiWebClient;

    @Value("${ai.api.secret-key:}")
    private String apiSecretKey;

    // 시간표 이미지 → AI 파싱 요청, classes 배열 반환
    public TimetableParseResponse parseTimetable(byte[] imageBytes, String fileName, String contentType) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(imageBytes) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                })
                .contentType(MediaType.parseMediaType(contentType));

        try {
            return productAiWebClient.post()
                    .uri("/api/v1/timetable/parse-timetable")
                    .headers(this::applyApiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(TimetableParseResponse.class)
                    .block(Duration.ofSeconds(120));
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            log.error("AI 서버 오류 - status={}, body={}, apiKeySet={}", e.getStatusCode(), e.getResponseBodyAsString(), StringUtils.hasText(apiSecretKey));
            throw e;
        }
    }

    private void applyApiKey(HttpHeaders headers) {
        if (StringUtils.hasText(apiSecretKey)) {
            headers.set("x-api-key", apiSecretKey);
        }
    }

    public record TimetableParseResponse(
            List<ClassEntry> classes,
            Integer count
    ) {}

    public record ClassEntry(
            String name,
            String day,
            String start_time,
            String end_time,
            String location
    ) {}
}

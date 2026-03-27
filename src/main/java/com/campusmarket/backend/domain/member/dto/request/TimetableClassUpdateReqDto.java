package com.campusmarket.backend.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record TimetableClassUpdateReqDto(
        String name,
        String day,

        @JsonProperty("start_time")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시작 시간 형식이 올바르지 않습니다. (예: 09:00)")
        String startTime,

        @JsonProperty("end_time")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "종료 시간 형식이 올바르지 않습니다. (예: 18:00)")
        String endTime,

        String location
) {}

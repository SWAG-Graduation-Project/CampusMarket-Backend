package com.campusmarket.backend.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TimetableClassResDto(
        String name,
        String day,

        @JsonProperty("start_time")
        String startTime,

        @JsonProperty("end_time")
        String endTime,

        String location
) {}

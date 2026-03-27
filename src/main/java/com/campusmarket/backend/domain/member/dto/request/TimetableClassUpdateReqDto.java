package com.campusmarket.backend.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TimetableClassUpdateReqDto(
        String name,
        String day,

        @JsonProperty("start_time")
        String startTime,

        @JsonProperty("end_time")
        String endTime,

        String location
) {}

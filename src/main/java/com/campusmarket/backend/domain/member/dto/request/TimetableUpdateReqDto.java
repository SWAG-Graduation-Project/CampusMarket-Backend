package com.campusmarket.backend.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TimetableUpdateReqDto(

        @NotNull(message = "수업 목록은 필수입니다.")
        @Valid
        List<TimetableClassDto> classes

) {
    public record TimetableClassDto(

            String name,

            @NotBlank(message = "요일은 필수입니다.")
            String day,

            @NotBlank(message = "시작 시간은 필수입니다.")
            @JsonProperty("start_time")
            String startTime,

            @NotBlank(message = "종료 시간은 필수입니다.")
            @JsonProperty("end_time")
            String endTime,

            String location
    ) {}
}

package com.campusmarket.backend.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LockerUpdateReqDto(

        @NotBlank(message = "건물명은 필수입니다.")
        String building,

        @NotBlank(message = "층은 필수입니다.")
        String floor,

        @NotBlank(message = "학과명은 필수입니다.")
        String major,

        @NotNull(message = "그룹 번호는 필수입니다.")
        @Positive(message = "그룹 번호는 양수여야 합니다.")
        Integer lockerGroup,

        @NotNull(message = "행 번호는 필수입니다.")
        @Positive(message = "행 번호는 양수여야 합니다.")
        Integer row,

        @NotNull(message = "열 번호는 필수입니다.")
        @Positive(message = "열 번호는 양수여야 합니다.")
        Integer col
) {}

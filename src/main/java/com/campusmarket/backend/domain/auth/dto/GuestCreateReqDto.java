package com.campusmarket.backend.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record GuestCreateReqDto(
        @NotBlank(message = "guestUuid는 필수입니다.")
        String guestUuid
) {
}

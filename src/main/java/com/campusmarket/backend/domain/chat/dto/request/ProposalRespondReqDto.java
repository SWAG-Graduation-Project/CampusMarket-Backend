package com.campusmarket.backend.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProposalRespondReqDto(
        @NotNull(message = "수락 여부는 필수입니다.")
        Boolean accept
) {}

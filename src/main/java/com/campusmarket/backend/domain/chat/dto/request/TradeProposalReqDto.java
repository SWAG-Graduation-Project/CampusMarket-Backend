package com.campusmarket.backend.domain.chat.dto.request;

import com.campusmarket.backend.domain.chat.constant.ProposalType;
import jakarta.validation.constraints.NotNull;

public record TradeProposalReqDto(
        @NotNull(message = "제안 유형은 필수입니다.")
        ProposalType proposalType
) {}

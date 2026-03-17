package com.campusmarket.backend.dto;

import com.campusmarket.backend.model.MemberRole;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor

public class MemberRequest {

    private String nickname;
    private MemberRole role;

}

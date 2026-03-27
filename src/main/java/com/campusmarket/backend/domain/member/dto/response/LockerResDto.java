package com.campusmarket.backend.domain.member.dto.response;

import com.campusmarket.backend.domain.member.entity.Member;

public record LockerResDto(
        String lockerName,
        String building,
        String floor,
        String major,
        Integer lockerGroup,
        Integer row,
        Integer col
) {
    public static LockerResDto from(Member member) {
        return new LockerResDto(
                member.getLockerName(),
                member.getLockerBuilding(),
                member.getLockerFloor(),
                member.getLockerMajor(),
                member.getLockerGroup(),
                member.getLockerRow(),
                member.getLockerCol()
        );
    }
}

package com.campusmarket.backend.domain.member.dto.response;

public record TimetableParseResDto(
        String timetableData   // JSON: {"classes": [...]}
) {
    public static TimetableParseResDto of(String timetableData) {
        return new TimetableParseResDto(timetableData);
    }
}

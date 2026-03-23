package com.campusmarket.backend.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private String code;     // 성공/실패 코드
    private String message;  // 메시지
    private T data;          // 실제 데이터

    // 성공 응답 (data 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "요청 성공", data);
    }

    // 성공 응답 (data 없음)
    public static ApiResponse<Void> success() {
        return new ApiResponse<>("SUCCESS", "요청 성공", null);
    }

    // 실패 응답
    public static ApiResponse<Void> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
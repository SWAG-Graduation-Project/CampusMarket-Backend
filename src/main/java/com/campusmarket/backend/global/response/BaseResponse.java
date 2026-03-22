package com.campusmarket.backend.global.response;

import lombok.Getter;

@Getter
public class BaseResponse<T> {

    private final boolean isSuccess;
    private final String code;
    private final String message;
    private final T result;

    private BaseResponse(boolean isSuccess, String code, String message, T result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public static <T> BaseResponse<T> onSuccess(T result) {
        return new BaseResponse<>(true, "COMMON_200", "성공입니다.", result);
    }

    public static <T> BaseResponse<T> onFailure(String code, String message) {
        return new BaseResponse<>(false, code, message, null);
    }
}
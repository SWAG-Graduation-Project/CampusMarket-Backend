package com.campusmarket.backend.global.exception;

import com.campusmarket.backend.domain.member.exception.MemberException;
import com.campusmarket.backend.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<BaseResponse<Void>> handleMemberException(MemberException exception) {
        BaseErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .badRequest()
                .body(BaseResponse.onFailure("COMMON_400", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled Exception occurred.", exception);

        return ResponseEntity
                .internalServerError()
                .body(BaseResponse.onFailure("COMMON_500", "서버 내부 오류입니다."));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BaseResponse<Void>> handleMissingRequestHeaderException(
            MissingRequestHeaderException exception
    ) {
        return ResponseEntity
                .badRequest()
                .body(BaseResponse.onFailure("COMMON_400", "필수 헤더가 누락되었습니다."));
    }
}

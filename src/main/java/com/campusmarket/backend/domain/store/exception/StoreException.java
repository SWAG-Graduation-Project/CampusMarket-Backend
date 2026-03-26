package com.campusmarket.backend.domain.store.exception;

import com.campusmarket.backend.domain.store.constant.StoreErrorCode;
import lombok.Getter;

@Getter
public class StoreException extends RuntimeException {

    private final StoreErrorCode errorCode;

    public StoreException(StoreErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
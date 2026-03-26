package com.campusmarket.backend.domain.wishlist.exception;

import com.campusmarket.backend.domain.wishlist.constant.WishlistErrorCode;
import lombok.Getter;

@Getter
public class WishlistException extends RuntimeException {

    private final WishlistErrorCode errorCode;

    public WishlistException(WishlistErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
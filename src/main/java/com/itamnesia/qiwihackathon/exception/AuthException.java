package com.itamnesia.qiwihackathon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends ApplicationException {

    public AuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

}

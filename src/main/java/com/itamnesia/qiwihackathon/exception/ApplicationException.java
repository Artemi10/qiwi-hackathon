package com.itamnesia.qiwihackathon.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public ApiExceptionBody toApiExceptionBody() {
        return new ApiExceptionBody(message, status.value());
    }

}

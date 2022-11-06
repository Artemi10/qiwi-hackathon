package com.itamnesia.qiwihackathon.configuration.handlers;

import com.itamnesia.qiwihackathon.exception.ApiExceptionBody;
import com.itamnesia.qiwihackathon.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiExceptionBody> resourceNotFoundException(ApplicationException exception) {
        var status = exception.getStatus();
        var message = new ApiExceptionBody(exception.getMessage(), status.value());
        return new ResponseEntity<>(message, status);
    }

}

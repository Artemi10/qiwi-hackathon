package com.itamnesia.qiwihackathon.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itamnesia.qiwihackathon.exception.ApiExceptionBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var status = HttpStatus.UNAUTHORIZED.value();
        response.addHeader("Content-type", "application/json");
        response.setStatus(status);
        var message = new ApiExceptionBody(authException.getMessage(), status);
        var writer = response.getWriter();
        objectMapper.writeValue(writer, message);
        writer.flush();
    }
}

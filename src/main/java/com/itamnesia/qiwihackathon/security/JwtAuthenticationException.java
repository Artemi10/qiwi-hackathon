package com.itamnesia.qiwihackathon.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException() {
        super("Access token is invalid");
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}

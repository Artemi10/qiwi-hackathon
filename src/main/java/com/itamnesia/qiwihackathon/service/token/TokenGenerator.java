package com.itamnesia.qiwihackathon.service.token;

import org.springframework.stereotype.Service;

@Service
public interface TokenGenerator {
    String generateToken();
}

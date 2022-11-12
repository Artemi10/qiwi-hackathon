package com.itamnesia.qiwihackathon.service.token;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenGeneratorImpl implements TokenGenerator {
    private final SecureRandom secureRandom;
    private final Base64.Encoder encoder;

    public TokenGeneratorImpl() {
        this.secureRandom = new SecureRandom();
        this.encoder = Base64.getUrlEncoder();
    }

    @Override
    public String generateToken() {
        var tokenLength = 32;
        var byteArray = new byte[tokenLength / 4 * 3];
        secureRandom.nextBytes(byteArray);
        return encoder.encodeToString(byteArray);
    }

}

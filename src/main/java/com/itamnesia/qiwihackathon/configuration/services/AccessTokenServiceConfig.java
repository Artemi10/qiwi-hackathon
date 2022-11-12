package com.itamnesia.qiwihackathon.configuration.services;

import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.security.token.AccessTokenServiceImpl;
import com.itamnesia.qiwihackathon.service.time.TimeService;
import com.itamnesia.qiwihackathon.service.token.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AccessTokenServiceConfig {
    @Value("${jwt.access.expired}")
    private Long paymentPeriod;
    @Value("${jwt.payment.expired}")
    private Long accessPeriod;
    private final TimeService timeService;
    private final TokenGenerator tokenGenerator;

    @Bean
    public AccessTokenService applicationAccessTokenService() {
        return new AccessTokenServiceImpl(accessPeriod, timeService, tokenGenerator);
    }

    @Bean
    public AccessTokenService paymentAccessTokenService() {
        return new AccessTokenServiceImpl(paymentPeriod, timeService, tokenGenerator);
    }
}

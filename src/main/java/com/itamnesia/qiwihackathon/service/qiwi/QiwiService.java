package com.itamnesia.qiwihackathon.service.qiwi;

import org.springframework.stereotype.Service;

@Service
public interface QiwiService {
    void createPaymentRequest(long id);

    String confirmPayment(long id, String code);

    void sendPayment(long shopId, String token);
}
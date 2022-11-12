package com.itamnesia.qiwihackathon.service.account;

import org.springframework.stereotype.Service;

@Service
public interface QiwiService {
    void createPaymentRequest(long id);

    String confirmPayment(long id, String code);

    void sendPayment(long id, String code);
}

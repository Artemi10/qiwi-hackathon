package com.itamnesia.qiwihackathon.service.qiwi;

import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.transfer.auth.TokensDTO;
import org.springframework.stereotype.Service;

@Service
public interface QiwiService {
    void createPaymentRequest(long id);

    TokensDTO confirmPayment(long id, String code);

    void sendPayment(long shopId, String token, long money);

    void deletePayment(User user);
}

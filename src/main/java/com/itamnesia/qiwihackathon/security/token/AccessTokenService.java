package com.itamnesia.qiwihackathon.security.token;

import com.itamnesia.qiwihackathon.model.user.User;
import org.springframework.stereotype.Service;

@Service
public interface AccessTokenService {
    String createAccessToken(User user);

    String createAccessPaymentToken(User user);

    String getLogin(String accessToken);

    String getRole(String accessToken);

    boolean isValid(String accessToken);

}

package com.itamnesia.qiwihackathon.service.auth;

import com.itamnesia.qiwihackathon.transfer.auth.AuthDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    TokenDTO signUp(AuthDTO signUp);

    TokenDTO logIn(AuthDTO logInUser);
}

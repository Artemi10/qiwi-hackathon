package com.itamnesia.qiwihackathon.service.auth;

import com.itamnesia.qiwihackathon.transfer.auth.CodeDTO;
import com.itamnesia.qiwihackathon.transfer.auth.LogInDTO;
import com.itamnesia.qiwihackathon.transfer.auth.SignUpDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    TokenDTO signUp(SignUpDTO signUpUser);

    TokenDTO logIn(LogInDTO logInUser);

    TokenDTO confirmClient(String phoneNumber, CodeDTO codeDTO);

    TokenDTO confirmShop(String phoneNumber, CodeDTO codeDTO);
}

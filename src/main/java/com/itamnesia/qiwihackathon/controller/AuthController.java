package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.service.auth.AuthService;
import com.itamnesia.qiwihackathon.transfer.auth.AuthDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/log-in")
    public TokenDTO logIn(@RequestBody AuthDTO logInUser) {
        return authService.logIn(logInUser);
    }

    @PostMapping("/sign-up")
    public TokenDTO signUp(@RequestBody AuthDTO signUpUser) {
        return authService.signUp(signUpUser);
    }

}

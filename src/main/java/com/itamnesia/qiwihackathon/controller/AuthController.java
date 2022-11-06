package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.service.auth.AuthService;
import com.itamnesia.qiwihackathon.transfer.auth.AuthDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation("Log in an existed user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "Email and password combination is incorrect")
    })
    public TokenDTO logIn(@RequestBody AuthDTO logInUser) {
        return authService.logIn(logInUser);
    }

    @PostMapping("/sign-up")
    @ApiOperation("Sign up a new user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "User has already been registered")
    })
    public TokenDTO signUp(@RequestBody AuthDTO signUpUser) {
        return authService.signUp(signUpUser);
    }

}

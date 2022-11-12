package com.itamnesia.qiwihackathon.service.auth;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.user.Role;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.transfer.auth.CodeDTO;
import com.itamnesia.qiwihackathon.transfer.auth.LogInDTO;
import com.itamnesia.qiwihackathon.transfer.auth.SignUpDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;

    @Override
    public TokenDTO signUp(SignUpDTO signUpUser) {
        if (userRepository.existsByPhoneNumber(signUpUser.phoneNumber())) {
            throw new AuthException("User has already been registered");
        }
        var newUser = User.builder()
                .login(signUpUser.login())
                .password(passwordEncoder.encode(signUpUser.password()))
                .phoneNumber(signUpUser.phoneNumber())
                .confirmToken(passwordEncoder.encode("8763"))
                .role(Role.NOT_CONFIRMED)
                .build();
        var savedUser = userRepository.save(newUser);
        var tokenStr = accessTokenService.createAccessToken(savedUser);
        return new TokenDTO(tokenStr);
    }

    @Override
    public TokenDTO logIn(LogInDTO logInUser) {
        var authUser = userRepository.findByPhoneNumber(logInUser.phoneNumber())
                .filter(user -> passwordEncoder.matches(logInUser.password(), user.getPassword()))
                .orElseThrow(() -> new AuthException("Credentials are invalid"));
        var tokenStr = accessTokenService.createAccessToken(authUser);
        return new TokenDTO(tokenStr);
    }

    @Override
    public TokenDTO confirmClient(String phoneNumber, CodeDTO codeDTO) {
        var authUser = userRepository.findByPhoneNumber(phoneNumber)
                .filter(user -> passwordEncoder.matches(codeDTO.code(), user.getConfirmToken()))
                .orElseThrow(() -> new AuthException("Code are incorrect"));
        authUser.setConfirmToken(null);
        authUser.setRole(Role.CLIENT);
        var user = userRepository.save(authUser);
        var tokenStr = accessTokenService.createAccessToken(user);
        return new TokenDTO(tokenStr);
    }

    @Override
    public TokenDTO confirmShop(String phoneNumber, CodeDTO codeDTO) {
        var authUser = userRepository.findByPhoneNumber(phoneNumber)
                .filter(user -> passwordEncoder.matches(codeDTO.code(), user.getConfirmToken()))
                .orElseThrow(() -> new AuthException("Code are incorrect"));
        authUser.setConfirmToken(null);
        authUser.setRole(Role.SHOP);
        var user = userRepository.save(authUser);
        var tokenStr = accessTokenService.createAccessToken(user);
        return new TokenDTO(tokenStr);
    }

    @Override
    public TokenDTO createShopAccount(long id) {
        var user = userRepository.findById(id)
                        .orElseThrow(() -> new AuthException("Code are incorrect"));
        user.setRole(Role.SHOP);
        var savedUser = userRepository.save(user);
        var tokenStr = accessTokenService.createAccessToken(savedUser);
        return new TokenDTO(tokenStr);
    }
}

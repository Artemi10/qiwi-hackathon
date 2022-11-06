package com.itamnesia.qiwihackathon.service.auth;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.user.Role;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import com.itamnesia.qiwihackathon.transfer.auth.AuthDTO;
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
    public TokenDTO signUp(AuthDTO signUp) {
        if (userRepository.existsByLogin(signUp.login())) {
            throw new AuthException("User has already been registered");
        }
        var newUser = User.builder()
                .login(signUp.login())
                .password(passwordEncoder.encode(signUp.password()))
                .role(Role.ACTIVE)
                .build();
        var savedUser = userRepository.save(newUser);
        var tokenStr = accessTokenService.createAccessToken(savedUser);
        return new TokenDTO(tokenStr);
    }

    @Override
    public TokenDTO logIn(AuthDTO logInUser) {
        var autUser = userRepository.findByLogin(logInUser.login())
                .filter(user -> passwordEncoder.matches(logInUser.password(), user.getPassword()))
                .orElseThrow(() -> new AuthException("Credentials are invalid"));
        var tokenStr = accessTokenService.createAccessToken(autUser);
        return new TokenDTO(tokenStr);
    }
}

package com.itamnesia.qiwihackathon.service.user;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.service.token.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;

    @Override
    public User generatePayment(long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));
        user.setAccountId(tokenGenerator.generateToken());
        user.setRequestId(tokenGenerator.generateToken());
        return userRepository.save(user);
    }

    @Override
    public void deletePayment(User user) {
        user.setAccountId(null);
        user.setRequestId(null);
        userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));
    }
}

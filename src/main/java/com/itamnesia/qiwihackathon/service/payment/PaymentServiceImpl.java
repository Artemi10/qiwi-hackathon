package com.itamnesia.qiwihackathon.service.payment;

import com.itamnesia.qiwihackathon.exception.AuthException;
import com.itamnesia.qiwihackathon.model.user.Role;
import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.repository.UserRepository;
import com.itamnesia.qiwihackathon.service.token.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
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
    public User startPayment(User user, String paymentToken) {
        if (user.getRole().equals(Role.CLIENT)) {
            user.setRole(Role.CLIENT_PAYMENT);
        }
        else {
            user.setRole(Role.SHOP_PAYMENT);
        }
        user.setPaymentToken(paymentToken);
        return userRepository.save(user);
    }

    @Override
    public User deletePayment(User user) {
        user.setAccountId(null);
        user.setRequestId(null);
        if (user.getRole().equals(Role.CLIENT_PAYMENT)) {
            user.setRole(Role.CLIENT);
        }
        if (user.getRole().equals(Role.SHOP_PAYMENT)) {
            user.setRole(Role.SHOP);
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AuthException("User not found"));
    }
}

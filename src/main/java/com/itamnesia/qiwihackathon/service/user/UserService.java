package com.itamnesia.qiwihackathon.service.user;

import com.itamnesia.qiwihackathon.model.user.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User generatePayment(long id);

    User deletePayment(User user);

    User getUserById(long id);

    User startPayment(User user, String paymentToken);
}

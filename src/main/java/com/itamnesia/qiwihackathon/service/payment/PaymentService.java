package com.itamnesia.qiwihackathon.service.payment;

import com.itamnesia.qiwihackathon.model.Payment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PaymentService {
    List<Payment> findAllByShopId(long shopId);

    List<Payment> findAllByPurchaserId(long purchaserId);
}

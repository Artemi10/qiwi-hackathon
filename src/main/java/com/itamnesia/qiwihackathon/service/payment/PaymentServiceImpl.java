package com.itamnesia.qiwihackathon.service.payment;

import com.itamnesia.qiwihackathon.model.Payment;
import com.itamnesia.qiwihackathon.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    public List<Payment> findAllByShopId(long shopId) {
        return paymentRepository.findAllByShopId(shopId);
    }

    @Override
    public List<Payment> findAllByPurchaserId(long purchaserId) {
        return paymentRepository.findAllByPurchaserId(purchaserId);
    }
}

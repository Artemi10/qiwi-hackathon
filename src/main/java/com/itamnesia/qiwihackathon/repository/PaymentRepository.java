package com.itamnesia.qiwihackathon.repository;

import com.itamnesia.qiwihackathon.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByShopId(long shopId);

    List<Payment> findAllByPurchaserId(long purchaserId);
}

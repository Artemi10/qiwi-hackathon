package com.itamnesia.qiwihackathon.transfer.payment.transaction;

public record PaymentMethod(String type, String paymentToken) {
    public PaymentMethod(String paymentToken) {
        this("TOKEN", paymentToken);
    }
}

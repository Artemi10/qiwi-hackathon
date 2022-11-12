package com.itamnesia.qiwihackathon.transfer.payment;

import com.itamnesia.qiwihackathon.transfer.payment.token.Status;

public record PaymentResponse(String requestId, Status status) {}

package com.itamnesia.qiwihackathon.transfer.payment.confirmation;

import com.itamnesia.qiwihackathon.transfer.payment.token.PaymentTokenDTO;
import com.itamnesia.qiwihackathon.transfer.payment.token.Status;

public record PaymentConfirmationResponse(String requestId, Status status, PaymentTokenDTO token) {
}

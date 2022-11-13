package com.itamnesia.qiwihackathon.transfer;

import java.time.OffsetDateTime;

public record PaymentDTO(
        Long id,
        String paymentId,
        String billId,
        OffsetDateTime createdDateTime,
        String status,
        String amount,
        String currency,
        String shopName,
        String purchaserName
) { }

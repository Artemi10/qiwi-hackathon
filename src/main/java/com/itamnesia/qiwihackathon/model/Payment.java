package com.itamnesia.qiwihackathon.model;

import com.itamnesia.qiwihackathon.transfer.payment.token.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Payment {
    private String paymentId;
    private String billId;
    private OffsetDateTime createdDateTime;
    private Status status;

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", billId='" + billId + '\'' +
                ", createdDateTime=" + createdDateTime +
                ", status=" + status +
                '}';
    }
}

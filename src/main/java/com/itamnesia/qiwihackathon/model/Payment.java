package com.itamnesia.qiwihackathon.model;

import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.transfer.PaymentDTO;
import com.itamnesia.qiwihackathon.transfer.payment.token.Status;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.Amount;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "payment_id")
    private String paymentId;
    @Column(name = "bill_id")
    private String billId;
    @Column(name = "creation_time")
    private OffsetDateTime createdDateTime;
    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "value", column = @Column(name = "status"))
    })
    private Status status;
    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "currency", column = @Column(name = "currency")),
            @AttributeOverride(name = "value", column = @Column(name = "amount")),
    })
    private Amount amount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private User shop;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchaser_id")
    private User purchaser;

    public PaymentDTO toDTO() {
        return new PaymentDTO(
                id,
                paymentId,
                billId,
                createdDateTime,
                status.getValue(),
                amount.getValue(),
                amount.getCurrency(),
                shop.getShopName(),
                purchaser.getLogin()
        );
    }

}

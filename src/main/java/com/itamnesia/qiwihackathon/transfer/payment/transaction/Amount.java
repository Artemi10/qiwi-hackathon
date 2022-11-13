package com.itamnesia.qiwihackathon.transfer.payment.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Amount {
    private String currency;
    private String value;

    public Amount(long value) {
        this("RUB", value + ".00");
    }

}

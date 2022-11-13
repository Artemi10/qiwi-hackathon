package com.itamnesia.qiwihackathon.transfer.payment.token;

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
public class Status {
    private String value;
}

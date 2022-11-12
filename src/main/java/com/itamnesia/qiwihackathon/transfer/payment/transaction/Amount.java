package com.itamnesia.qiwihackathon.transfer.payment.transaction;

public record Amount(String currency, String value) {

    public Amount(long value) {
        this("RUB", value + ".00");
    }

}

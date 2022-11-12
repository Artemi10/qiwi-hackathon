package com.itamnesia.qiwihackathon.transfer.payment.transaction;

import java.text.DecimalFormat;

public record Amount(String currency, String value) {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".##");
    static {
        DECIMAL_FORMAT.setParseBigDecimal(true);
    }

    public Amount(long value) {
        this("RUB", DECIMAL_FORMAT.format(value));
    }
}

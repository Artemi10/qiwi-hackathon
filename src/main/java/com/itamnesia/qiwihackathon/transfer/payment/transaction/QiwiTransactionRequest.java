package com.itamnesia.qiwihackathon.transfer.payment.transaction;

public record QiwiTransactionRequest(Amount amount, Customer customer, PaymentMethod paymentMethod) { }

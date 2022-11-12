package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.security.details.UserPrincipal;
import com.itamnesia.qiwihackathon.service.qiwi.QiwiService;
import com.itamnesia.qiwihackathon.transfer.payment.transaction.TransactionRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/shop/account")
public class ShopAccountController {
    private final QiwiService qiwiService;

    @PostMapping
    public void sendTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody TransactionRequest request
    ) {
        qiwiService.sendPayment(userPrincipal.id(), request.token(), request.amount());
    }
}

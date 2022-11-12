package com.itamnesia.qiwihackathon.controller;


import com.itamnesia.qiwihackathon.security.details.UserPrincipal;
import com.itamnesia.qiwihackathon.service.account.QiwiService;
import com.itamnesia.qiwihackathon.transfer.auth.CodeDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/client/account")
public class ClientAccountController {
    private final QiwiService qiwiService;

    @GetMapping("/payment")
    @ApiOperation("Send payment confirmation")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "Can not send payment request"),
            @ApiResponse(code = 403, message = "Role is invalid (CLIENT)")
    })
    public void sendConfirmation(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        qiwiService.createPaymentRequest(userPrincipal.id());
    }

    @PostMapping("/payment/confirm")
    @ApiOperation("Confirm payment")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Payment token", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "Can not send payment confirmation request"),
            @ApiResponse(code = 403, message = "Role is invalid (CLIENT)")
    })
    public TokenDTO confirmPayment(
            @RequestBody CodeDTO codeDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        var paymentToken = qiwiService.confirmPayment(userPrincipal.id(), codeDTO.code());
        return new TokenDTO(paymentToken);
    }

}
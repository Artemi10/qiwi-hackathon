package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.security.details.UserPrincipal;
import com.itamnesia.qiwihackathon.service.qiwi.QiwiService;
import com.itamnesia.qiwihackathon.service.auth.AuthService;
import com.itamnesia.qiwihackathon.transfer.auth.CodeDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokensDTO;
import com.itamnesia.qiwihackathon.transfer.shop.ShopDTO;
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
    private final AuthService authService;

    @PostMapping("/shop")
    @ApiOperation("Create shop account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "User not found"),
            @ApiResponse(code = 403, message = "Role is invalid (CLIENT)")
    })
    public TokenDTO createShopAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody ShopDTO shopDTO
    ) {
        return authService.createShopAccount(userPrincipal.id(), shopDTO);
    }

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
    public TokensDTO confirmPayment(
            @RequestBody CodeDTO codeDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return qiwiService.confirmPayment(userPrincipal.id(), codeDTO.code());
    }

}

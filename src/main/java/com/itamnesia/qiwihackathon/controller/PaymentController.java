package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.model.Payment;
import com.itamnesia.qiwihackathon.repository.PaymentRepository;
import com.itamnesia.qiwihackathon.security.details.UserPrincipal;
import com.itamnesia.qiwihackathon.transfer.PaymentDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentRepository paymentRepository;

    @GetMapping("/sales")
    @ApiOperation("Get sales")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = PaymentDTO.class),
            @ApiResponse(code = 403, message = "Role is invalid (SHOP)")
    })
    public List<PaymentDTO> getSales(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return paymentRepository.findAllByShopId(userPrincipal.id())
                .stream()
                .map(Payment::toDTO)
                .toList();
    }

    @GetMapping("/purchases")
    @ApiOperation("Get purchases")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = PaymentDTO.class),
            @ApiResponse(code = 403, message = "Role is invalid (SHOP AND CLIENT)")
    })
    public List<PaymentDTO> getPurchases(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return paymentRepository.findAllByPurchaserId(userPrincipal.id())
                .stream()
                .map(Payment::toDTO)
                .toList();
    }

}

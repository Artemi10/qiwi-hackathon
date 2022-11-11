package com.itamnesia.qiwihackathon.controller;

import com.itamnesia.qiwihackathon.security.details.UserPrincipal;
import com.itamnesia.qiwihackathon.service.auth.AuthService;
import com.itamnesia.qiwihackathon.transfer.auth.CodeDTO;
import com.itamnesia.qiwihackathon.transfer.auth.TokenDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/confirmation")
public class ConfirmationController {
    private final AuthService authService;

    @PostMapping("/client")
    @ApiOperation("Confirm client user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "Code are incorrect")
    })
    public TokenDTO confirmClient(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CodeDTO codeDTO
    ) {
        return authService.confirmClient(userPrincipal.getUsername(), codeDTO);
    }

    @PostMapping("/shop")
    @ApiOperation("Confirm shop user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = TokenDTO.class),
            @ApiResponse(code = 401, message = "Code are incorrect")
    })
    public TokenDTO confirmShop(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CodeDTO codeDTO
    ) {
        return authService.confirmShop(userPrincipal.getUsername(), codeDTO);
    }
}

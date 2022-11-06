package com.itamnesia.qiwihackathon.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @GetMapping("/test")
    @ApiOperation("Test endpoint")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 401, message = "User is unauthorized")
    })
    public String test() {
        return "Test";
    }

}

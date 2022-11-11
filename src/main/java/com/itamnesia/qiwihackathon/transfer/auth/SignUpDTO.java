package com.itamnesia.qiwihackathon.transfer.auth;

import javax.validation.constraints.Pattern;

public record SignUpDTO(
        String login,
        @Pattern(regexp = "\\S*(?=\\S{6,})\\S*", message = "Password must be strong")
        String password,
        String phoneNumber
) { }

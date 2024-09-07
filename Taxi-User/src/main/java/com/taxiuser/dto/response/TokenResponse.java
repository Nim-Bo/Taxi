package com.taxiuser.dto.response;

public record TokenResponse(
        String token,
        Integer expire
) {
}

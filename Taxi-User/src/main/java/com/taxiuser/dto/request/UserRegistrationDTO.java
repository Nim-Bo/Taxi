package com.taxiuser.dto.request;

public record UserRegistrationDTO(
        String username,
        String password,
        String confirmPassword,
        String firstName,
        String lastName
) {
}

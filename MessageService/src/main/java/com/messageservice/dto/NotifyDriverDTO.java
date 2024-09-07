package com.messageservice.dto;

public record NotifyDriverDTO(
        String firstName,
        String lastName,
        Integer price,
        Long driverUserId,
        String phone
) {
}


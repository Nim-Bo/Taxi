package com.taxiuser.dto.response;

public record NotifyDriverDTO(
        String firstName,
        String lastName,
        Integer price,
        Long driverUserId
) {
}

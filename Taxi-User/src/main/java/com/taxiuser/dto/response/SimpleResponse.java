package com.taxiuser.dto.response;

public record SimpleResponse(
        Integer statusCode,
        String message
) {
}

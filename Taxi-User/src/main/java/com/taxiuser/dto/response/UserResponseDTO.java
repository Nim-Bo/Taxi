package com.taxiuser.dto.response;

import com.taxiuser.model.Order;

import java.util.Date;
import java.util.List;

public record UserResponseDTO(
       Long id,
       String firstName,
       String lastName,
       String phone,
       Date createdAt,
       Integer balance,
       String status,
       List<Order> orders
) {
}

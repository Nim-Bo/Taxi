package com.taxiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.dto.request.OrderRequestDTO;
import com.taxiuser.dto.response.OrderResponseDTO;
import com.taxiuser.exception.DriverNotFound;
import com.taxiuser.exception.IncompleteOrderExists;
import com.taxiuser.exception.OrderNotFound;
import com.taxiuser.exception.UserIsDriving;
import com.taxiuser.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderController {

    OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDTO> newOrder(@RequestBody OrderRequestDTO order, @RequestHeader("Authorization") String authentication) throws IncompleteOrderExists, UserIsDriving, DriverNotFound, JsonProcessingException {
        OrderResponseDTO response = orderService.order(order, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/myOrder")
    public ResponseEntity<OrderResponseDTO> userOrderInfo(@RequestHeader("Authorization") String authentication) throws OrderNotFound {
        OrderResponseDTO response = orderService.userOrderInfo(authentication);
        return ResponseEntity.ok(response);
    }

}

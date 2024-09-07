package com.taxiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PaymentController {

    PaymentService paymentService;

    @PutMapping("/myOrder")
    public ResponseEntity<String> payOrder(@RequestHeader("Authorization") String authorization) throws BadRequestException, JsonProcessingException {
        String response = paymentService.payOrder(authorization);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/charge")
    public ResponseEntity<String> charge(@RequestParam Integer amount, @RequestHeader("Authorization") String authorization) throws BadRequestException, JsonProcessingException {
        String response = paymentService.charge(amount, authorization);
        return ResponseEntity.ok(response);
    }

}

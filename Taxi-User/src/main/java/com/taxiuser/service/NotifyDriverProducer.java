package com.taxiuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxiuser.dto.response.NotifyDriverDTO;
import com.taxiuser.model.Order;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotifyDriverProducer {

    @Value("${rabbitmq.exchange.name}")
    String exchangeName;

    @Value("${rabbitmq.routing.notify-driver}")
    String notifyDriverKey;

    final RabbitTemplate rabbitTemplate;
    final ObjectMapper objectMapper;

    public void notify(NotifyDriverDTO notifyDriverDTO) throws JsonProcessingException {
        byte[] msg = objectMapper.writeValueAsBytes(notifyDriverDTO);
        rabbitTemplate.convertAndSend(exchangeName, notifyDriverKey, msg);
    }

}

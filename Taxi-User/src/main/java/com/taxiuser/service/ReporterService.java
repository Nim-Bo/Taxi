package com.taxiuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxiuser.dto.response.NotifyDriverDTO;
import com.taxiuser.dto.response.ReportDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReporterService {

    @Value("${rabbitmq.exchange.name}")
    String exchangeName;

    @Value("${rabbitmq.routing.reporter}")
    String reporterKey;

    final RabbitTemplate rabbitTemplate;
    final ObjectMapper objectMapper;

    public void report(ReportDTO reportDTO) throws JsonProcessingException {
        byte[] msg = objectMapper.writeValueAsBytes(reportDTO);
        rabbitTemplate.convertAndSend(exchangeName, reporterKey, msg);
    }

}

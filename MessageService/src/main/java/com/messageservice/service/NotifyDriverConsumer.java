package com.messageservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messageservice.dto.NotifyDriverDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotifyDriverConsumer {

    ObjectMapper objectMapper;

    @RabbitListener(queues = "${rabbitmq.queue.notify-driver}")
    public void notifyDriver(byte[] msg) throws IOException {
        NotifyDriverDTO notifyDriverDTO = objectMapper.readValue(msg, NotifyDriverDTO.class);
        log.info("New Message Sent To Driver Id "
                + notifyDriverDTO.driverUserId()
                + " , Phone : "
                + notifyDriverDTO.phone()
                + ", Message :"
                + "\nYou have a new traveler !\n"
                + notifyDriverDTO.firstName() + " " + notifyDriverDTO.lastName()
                + "\nPrice : " + notifyDriverDTO.price());
    }

}

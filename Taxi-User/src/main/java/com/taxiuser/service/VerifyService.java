package com.taxiuser.service;

import com.taxiuser.dto.response.SimpleResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VerifyService {

    SMSService smsService;
    RedissonClient redisson;

    public Boolean sendVerify(String phone, Integer code) throws IOException, InterruptedException {
        try {
            HttpResponse<String> response = smsService.sendSMS(phone, "Salam Khedmate Shoma !\nCode : " + code + "\nLotfan in code ra dar ekhtiare hichkas gharar nadahid.");
            log.info("{} {}", code, phone);
//            return response.statusCode() == 200;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public SimpleResponse verify(String phone, Integer code) {
        RMapCache<String, Integer> verifyCodes = redisson.getMapCache("verify-codes");
        if (!verifyCodes.containsKey(phone))
            return new SimpleResponse(400, "Verification expired or does not exist");

        RMapCache<String, Integer> verifyChances = redisson.getMapCache("verify-chances");
        Integer userVerifyChance = verifyChances.get(phone);
        if (userVerifyChance == 0)
            return new SimpleResponse(429, "Too many requests, try again later");

        verifyChances.fastPut(phone, userVerifyChance-1);
        if (!code.equals(verifyCodes.get(phone)))
            return new SimpleResponse(400, "Invalid verification code");

        return new SimpleResponse(200, "OK");
    }

}

package com.taxiuser.service;

import com.taxiuser.exception.InvalidVerificationCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
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

    public Boolean verify(String phone, Integer code) throws BadRequestException {
        RMapCache<String, Integer> verifyCodes = redisson.getMapCache("verify-codes");
        if (!verifyCodes.containsKey(phone))
            throw new BadRequestException("Verification expired or does not exist");

        RMapCache<String, Integer> verifyChances = redisson.getMapCache("verify-chances");
        Integer userVerifyChance = verifyChances.get(phone);
        if (userVerifyChance == 0)
            throw new BadRequestException("Too many requests, try again later");

        verifyChances.fastPut(phone, userVerifyChance-1);
        if (!code.equals(verifyCodes.get(phone)))
            throw new InvalidVerificationCode();

        return true;
    }

}

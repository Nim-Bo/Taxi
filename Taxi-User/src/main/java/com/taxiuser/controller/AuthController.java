package com.taxiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.dto.request.UserLoginDTO;
import com.taxiuser.dto.request.UserRegistrationDTO;
import com.taxiuser.dto.response.UserResponseDTO;
import com.taxiuser.exception.InvalidVerificationCode;
import com.taxiuser.exception.PhoneNumberInUse;
import com.taxiuser.exception.RegistrationFailed;
import com.taxiuser.exception.SMSFailed;
import com.taxiuser.dto.response.TokenResponse;
import com.taxiuser.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {

    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestParam String phone) throws PhoneNumberInUse, SMSFailed, RegistrationFailed {
        String response = authService.signUp(phone);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenResponse> verify(@RequestParam String phone, Integer code) throws InvalidVerificationCode {
        TokenResponse response = authService.verify(phone, code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup/complete")
    public ResponseEntity<UserResponseDTO> completeSignUp(@RequestBody UserRegistrationDTO userRegistrationDTO, @RequestHeader("Authorization") String authorization) throws RegistrationFailed, JsonProcessingException {
        UserResponseDTO response = authService.completeSignup(userRegistrationDTO, authorization);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody UserLoginDTO login) throws BadRequestException {
        TokenResponse response = authService.login(login);
        return ResponseEntity.ok(response);
    }

}

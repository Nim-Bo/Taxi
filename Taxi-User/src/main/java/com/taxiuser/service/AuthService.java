package com.taxiuser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxiuser.dto.request.UserLoginDTO;
import com.taxiuser.dto.request.UserRegistrationDTO;
import com.taxiuser.dto.response.*;
import com.taxiuser.enums.ReportType;
import com.taxiuser.enums.Role;
import com.taxiuser.exception.*;
import com.taxiuser.mapper.UserMapper;
import com.taxiuser.model.User;
import com.taxiuser.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    RedissonClient redisson;
    UserRepository userRepository;
    VerifyService verifyService;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    CustomUserDetailsService customUserDetailsService;
    ReporterService reporterService;

    public String signUp(String phone) throws PhoneNumberInUse, SMSFailed, RegistrationFailed {
        if (userRepository.existsByPhone(phone))
            throw new RegistrationFailed("Phone number already signed up, try to login");
        if (!phone.startsWith("0"))
            throw new RegistrationFailed("Phone number should start with 0");
        if (phone.length() != 11)
            throw new RegistrationFailed("Invalid phone number");
        RLock lock = redisson.getLock(phone);
        try {
            if (lock.tryLock(120, TimeUnit.SECONDS)) {
                Random random = new Random();
                Integer code = 10000 + random.nextInt(90000);
                boolean sendVerify = verifyService.sendVerify(phone, code);
                if(!sendVerify)
                    throw new RegistrationFailed("Invalid phone number");
                RMapCache<String, Integer> verifyCodes = redisson.getMapCache("verify-codes");
                verifyCodes.put(phone, code, 120, TimeUnit.SECONDS);
                RMapCache<String, Integer> verifyChances = redisson.getMapCache("verify-chances");
                verifyChances.put(phone, 4, 120, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new PhoneNumberInUse();
        } catch (IOException e) {
            throw new SMSFailed();
        } finally {
            lock.unlock();
        }
        return "Verification code has been sent";
    }

    public TokenResponse verify(String phone, Integer code) throws InvalidVerificationCode {
        SimpleResponse verification = verifyService.verify(phone, code);
        if (!verification.statusCode().equals(200))
            throw new InvalidVerificationCode();
        RSetCache<String> signupCache = redisson.getSetCache("signupCache");
        signupCache.add(phone, 30, TimeUnit.MINUTES);
        return new TokenResponse(jwtService.generateSignupToken(phone), 30);
    }

    public UserResponseDTO completeSignup(UserRegistrationDTO userRegistrationDTO, String authentication) throws RegistrationFailed, JsonProcessingException {
        String token = authentication.substring(jwtService.getStartIndex());
        String phone = jwtService.extractSubject(token);
        // Check Phone
        if(userRepository.existsByPhone(phone))
            throw new RegistrationFailed("Phone number already registered, try to login");
        RLock lock = redisson.getLock("signup-lock" + phone);
        User userWithId = null;
        try {
            if (lock.tryLock(300, TimeUnit.MILLISECONDS)) {
                // Check Username
                if (userRepository.existsByUsername(userRegistrationDTO.username().toLowerCase()))
                    throw new RegistrationFailed("Username already exists");
                if (userRegistrationDTO.username().isEmpty())
                    throw new RegistrationFailed("Username cannot be empty");
                if (userRegistrationDTO.username().length() < 4)
                    throw new RegistrationFailed("Username must be at least 4 characters");
                if (userRegistrationDTO.username().length() > 16)
                    throw new RegistrationFailed("Username must be at most 16 characters");
                if (Character.isDigit(userRegistrationDTO.username().charAt(0)))
                    throw new RegistrationFailed("Username must not start with a number");
                // Check Password
                if (userRegistrationDTO.password().isEmpty())
                    throw new RegistrationFailed("Password cannot be empty");
                if (userRegistrationDTO.password().length() < 6)
                    throw new RegistrationFailed("Password must be at least 6 characters");
                if (userRegistrationDTO.password().length() > 16)
                    throw new RegistrationFailed("Password must be at most 16 characters");
                if (!userRegistrationDTO.password().equals(userRegistrationDTO.confirmPassword()))
                    throw new RegistrationFailed("Passwords do not match");
                // Check Name
                if (userRegistrationDTO.firstName().isEmpty())
                    throw new RegistrationFailed("First name cannot be empty");
                if (userRegistrationDTO.lastName().isEmpty())
                    throw new RegistrationFailed("Last name cannot be empty");
                // Registration
                User user = new User();
                user.setUsername(userRegistrationDTO.username().toLowerCase());
                user.setPassword(passwordEncoder.encode(userRegistrationDTO.password()));
                user.setRole(Role.ROLE_USER);
                user.setFirstName(userRegistrationDTO.firstName());
                user.setLastName(userRegistrationDTO.lastName());
                user.setPhone(phone);
                user.setBalance(0);
                user.setStatus("free");
                user.setCreatedAt(Date.from(Instant.now()));
                user.setUpdatedAt(Date.from(Instant.now()));
                userWithId = userRepository.save(user);
            }
        } catch (NullPointerException e) {
            throw new RegistrationFailed("Invalid data");
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to complete signup");
        } finally {
            lock.unlock();
        }
        reporterService.report(new ReportDTO(ReportType.NEW_SIGNUP, new NewSignupReport(userWithId.getId())));
        return UserMapper.INSTANCE.userToUserDTO(userWithId);
    }

    public TokenResponse login(UserLoginDTO login) throws BadRequestException {
        User user = userRepository.findByUsername(login.username())
                .orElseThrow(() -> new BadRequestException("Invalid username"));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                login.username(), login.password(), user.getAuthorities()
        ));
        if(authentication.isAuthenticated())
            return new TokenResponse(jwtService.generateToken(customUserDetailsService.loadUserByUsername(login.username())), 30);
        throw new Unauthorized();
    }

}

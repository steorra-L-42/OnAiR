package me.onair.main.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.user.dto.PhoneVerifyResponse;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.dto.VerificationCodeRequest;
import me.onair.main.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    // 1. 휴대폰 인증 번호 요청
    @PostMapping("/phone-verification/verification-code")
    public ResponseEntity<PhoneVerifyResponse> requestVerificationCode(@RequestBody @Valid VerificationCodeRequest request) {
        log.info("UserController.requestVerificationCode request: {}", request);

        userService.requestVerificationCode(request);

        return ResponseEntity.ok().build();
    }

    // 2. 휴대폰 인증 번호 확인
    @PostMapping("/phone-verification")
    public ResponseEntity<Object> verifyPhoneNumber(){

        return null;
    }

    // 3. Username 중복 확인

    // 4. 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody @Valid SignupRequest request) {
        log.info("UserController.join request: {}", request);

        userService.signup(request);

        return ResponseEntity.ok().build();
    }

    // 7. 회원 정보 조회

    // 8. 닉네임 수정

    // 9. 프로필 수정

    // 12. FCM token 저장

}
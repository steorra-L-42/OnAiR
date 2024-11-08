package me.onair.main.domain.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.dto.VerificationCodeRequest;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.entity.VerificationCode;
import me.onair.main.domain.user.error.DuplicatePhoneNumberException;
import me.onair.main.domain.user.error.DuplicateUsername;
import me.onair.main.domain.user.error.VerificationCodeRequestExceedLimitException;
import me.onair.main.domain.user.repository.UserRepository;
import me.onair.main.domain.user.repository.VerificationCodeRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SMSService smsService;

    @Transactional
    public void requestVerificationCode(VerificationCodeRequest request) {

        // 이미 가입된 전화번호
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException();
        }

        // expiredAt이 24시간이 지나기 전인 모든 VerificationCode를 phoneNumber로 조회.
        // 전화번호 인증 횟수가 5 미만이어야 한다.
        int count = verificationCodeRepository.countAllByPhoneNumberAndExpiredAtAfter(request.getPhoneNumber(),
                LocalDateTime.now().minusDays(1));

        if(count >= 5) {
            throw new VerificationCodeRequestExceedLimitException();
        }

        String code = Integer.toString((int) (Math.random() * 900000) + 100000);
        verificationCodeRepository.save(VerificationCode.builder()
                .phoneNumber(request.getPhoneNumber())
                .code(code)
                .expiredAt(LocalDateTime.now().plusMinutes(3))
                .build());

        smsService.sendSMS(request.getPhoneNumber(), code);
    }

    @Transactional
    public void signup(SignupRequest request) {

        validateDuplicateUsername(request.getUsername()); // 중복되는 username이 있는지 확인하는 로직
        // TODO : 6자리 인증번호 확인

        encodePassword(request); // password를 암호화하는 로직

        userRepository.save(User.createNomalUser(request));
    }

    private void validateDuplicateUsername(String username) {
        if(userRepository.existsByUsername(username)) {
            throw new DuplicateUsername(username);
        }
    }

    private void encodePassword(SignupRequest request) {
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        request.encodePassword(encodedPassword);
    }
}
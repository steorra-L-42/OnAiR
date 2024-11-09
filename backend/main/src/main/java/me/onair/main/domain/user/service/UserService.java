package me.onair.main.domain.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.user.dto.CustomUserDetails;
import me.onair.main.domain.user.dto.PhoneVerifyRequest;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.dto.UserInfoResponse;
import me.onair.main.domain.user.dto.VerificationCodeRequest;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.entity.VerificationCode;
import me.onair.main.domain.user.error.DuplicatePhoneNumberException;
import me.onair.main.domain.user.error.DuplicateUsername;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.error.NotVerifiedPhoneNumberException;
import me.onair.main.domain.user.error.UsernameTooShortOrLongException;
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

        validateDuplicatePhoneNumber(request.getPhoneNumber());

        // expiredAt이 24시간이 지나기 전인 모든 VerificationCode를 phoneNumber로 조회.
        // 전화번호 인증 횟수가 5 미만이어야 한다.
        int count = verificationCodeRepository.countAllByPhoneNumberAndExpiredAtAfter(request.getPhoneNumber(),
                LocalDateTime.now().minusDays(1));

        if (count >= 5) {
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
    public boolean verifyPhoneNumber(PhoneVerifyRequest request) {
        VerificationCode verificationCode = verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter(
                request.getPhoneNumber(), request.getVerification(), LocalDateTime.now());

        if (verificationCode == null) {
            return false;
        }

        verificationCode.verify(); // 인증 완료
        return true;
    }

    public boolean checkDuplicatedUsername(String username) {

        // username이 0~40자
        username = username.trim();
        if (username.isEmpty() || username.length() > 40) {
            throw new UsernameTooShortOrLongException();
        }

        return !userRepository.existsByUsername(username);
    }

    @Transactional
    public void signup(SignupRequest request) {

        // 하룻동안 인증된 경우 통과
        VerificationCode verificationCode = verificationCodeRepository.findByPhoneNumberAndCodeAndExpiredAtAfter(
                request.getPhoneNumber(), request.getVerification(), LocalDateTime.now().minusDays(1));

        if (verificationCode == null || !verificationCode.isVerified()) {
            throw new NotVerifiedPhoneNumberException();
        }

        validateDuplicateUsername(request.getUsername()); // custom error code : B001
        validateDuplicatePhoneNumber(request.getPhoneNumber()); // custom error code : B002
        encodePassword(request); // password를 암호화하는 로직

        userRepository.save(User.createNormalUser(request));
    }

    public UserInfoResponse getUserInfo(CustomUserDetails customUserDetails) {

        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(NotExistUserException::new);

        return UserInfoResponse.from(user);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsername(username);
        }
    }

    private void validateDuplicatePhoneNumber(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicatePhoneNumberException();
        }
    }

    private void encodePassword(SignupRequest request) {
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        request.encodePassword(encodedPassword);
    }
}
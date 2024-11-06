package me.onair.main.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.user.dto.SignupRequestDto;
import me.onair.main.domain.user.error.DuplicateUsername;
import me.onair.main.domain.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signup(SignupRequestDto request) {

        validateDuplicateUsername(request.getUsername()); // 중복되는 username이 있는지 확인하는 로직

        encodePassword(request); // password를 암호화하는 로직

        userRepository.save(UserEntity.of(request));
    }

    private void validateDuplicateUsername(String username) {
        if(userRepository.existsByUsername(username)) {
            throw new DuplicateUsername(username);
        }
    }

    private void encodePassword(SignupRequestDto request) {
        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());
        request.encodePassword(encodedPassword);
    }
}
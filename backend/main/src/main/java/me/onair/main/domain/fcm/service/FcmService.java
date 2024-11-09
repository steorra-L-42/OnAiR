package me.onair.main.domain.fcm.service;

import lombok.RequiredArgsConstructor;
import me.onair.main.domain.fcm.dto.FcmSaveRequest;
import me.onair.main.domain.fcm.entity.FcmToken;
import me.onair.main.domain.fcm.repository.FcmRepository;
import me.onair.main.domain.user.entity.User;
import me.onair.main.domain.user.error.NotExistUserException;
import me.onair.main.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FcmService {

    private final FcmRepository fcmRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveToken(FcmSaveRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(NotExistUserException::new);

        FcmToken fcmToken = fcmRepository.save(FcmToken.from(request.getFcmToken()));
        user.setFcmToken(fcmToken);
    }
}

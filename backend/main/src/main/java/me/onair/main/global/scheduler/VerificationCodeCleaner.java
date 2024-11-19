package me.onair.main.global.scheduler;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import me.onair.main.domain.user.repository.VerificationCodeRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeCleaner {

    private final VerificationCodeRepository verificationCodeRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredVerificationCodes() {
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        verificationCodeRepository.deleteByExpiredAtBefore(twoDaysAgo);
    }
}

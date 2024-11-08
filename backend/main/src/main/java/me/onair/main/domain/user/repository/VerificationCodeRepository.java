package me.onair.main.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import me.onair.main.domain.user.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Integer countAllByPhoneNumberAndExpiredAtAfter(String phoneNumber, LocalDateTime time);

    List<VerificationCode> findAllByPhoneNumberAndExpiredAtAfter(String phoneNumber, LocalDateTime time);

    void deleteByExpiredAtBefore(LocalDateTime twoDaysAgo);

    boolean existsByPhoneNumberAndCodeAndExpiredAtAfter(String phoneNumber, String verification, LocalDateTime now);
}

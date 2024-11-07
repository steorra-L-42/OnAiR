package me.onair.main.domain.user.repository;

import me.onair.main.domain.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmTokenRepository extends JpaRepository<RefreshToken, Long> {
}

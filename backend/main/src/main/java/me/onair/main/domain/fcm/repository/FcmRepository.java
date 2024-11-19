package me.onair.main.domain.fcm.repository;

import me.onair.main.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends JpaRepository<FcmToken, Long> {
}

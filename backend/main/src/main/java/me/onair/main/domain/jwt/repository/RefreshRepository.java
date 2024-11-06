package me.onair.main.domain.jwt.repository;

import me.onair.main.domain.jwt.entity.RefreshToken;
import me.onair.main.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByValue(String value);

    void deleteAllByUser(User user);

    @Transactional
    void deleteByValue(String value);
}
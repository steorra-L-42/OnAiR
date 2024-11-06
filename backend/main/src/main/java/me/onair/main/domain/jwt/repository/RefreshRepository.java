package me.onair.main.domain.jwt.repository;

import me.onair.main.domain.user.entity.RefreshToken;
import me.onair.main.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByValue(String value);

    void deleteAllByUser(UserEntity userEntity);

    @Transactional
    void deleteByValue(String value);
}
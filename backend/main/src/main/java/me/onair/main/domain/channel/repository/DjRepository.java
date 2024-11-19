package me.onair.main.domain.channel.repository;

import me.onair.main.domain.channel.entity.Dj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DjRepository extends JpaRepository<Dj, Long> {
}

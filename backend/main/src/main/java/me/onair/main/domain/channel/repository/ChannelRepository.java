package me.onair.main.domain.channel.repository;

import java.util.List;
import java.util.Optional;
import me.onair.main.domain.channel.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    public Optional<Channel> findByUuid(String uuid);

    public List<Channel> findByIsEnded(boolean isEnded);

    public long countByIsEnded(boolean isEnded);
}

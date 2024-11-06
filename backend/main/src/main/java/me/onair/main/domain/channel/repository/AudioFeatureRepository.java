package me.onair.main.domain.channel.repository;

import me.onair.main.domain.channel.entity.AudioFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFeatureRepository extends JpaRepository<AudioFeature, Long> {
}

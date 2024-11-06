package me.onair.main.domain.story.repository;

import me.onair.main.domain.story.entity.StoryMusic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryMusicRepository extends JpaRepository<StoryMusic, Long> {
}

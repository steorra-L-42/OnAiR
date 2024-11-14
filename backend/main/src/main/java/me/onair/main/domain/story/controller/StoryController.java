package me.onair.main.domain.story.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.story.dto.StoryCreateRequest;
import me.onair.main.domain.story.service.StoryService;
import me.onair.main.domain.user.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/story")
@Slf4j
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/{channel_id}")
    public ResponseEntity<?> createStory(@PathVariable("channel_id") Long channelId,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @Valid @RequestBody StoryCreateRequest request) {
        log.info("StoryController.checkValidChannelId: {}", channelId);

        storyService.createStory(channelId, customUserDetails, request);

        return ResponseEntity.ok().build();
    }
}

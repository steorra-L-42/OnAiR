package me.onair.main.domain.channel.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.channel.dto.CreateNewChannelRequest;
import me.onair.main.domain.channel.dto.CreateNewChannelResponse;
import me.onair.main.domain.channel.service.ChannelService;
import me.onair.main.domain.user.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/channel")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<?> createNewChannel(
            @RequestBody @Valid CreateNewChannelRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        
        log.info("ChannelController.createNewChannel", request);
        CreateNewChannelResponse response = channelService.createNewChannel(request, customUserDetails);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}

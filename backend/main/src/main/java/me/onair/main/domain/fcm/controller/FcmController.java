package me.onair.main.domain.fcm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.onair.main.domain.fcm.dto.FcmSaveRequest;
import me.onair.main.domain.fcm.service.FcmService;
import me.onair.main.domain.user.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/fcm-token")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping
    public ResponseEntity<?> saveToken(@RequestBody @Valid FcmSaveRequest request,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        log.info("request: {}", request);

        fcmService.saveToken(request, customUserDetails.getId());

        return ResponseEntity.ok().build();
    }
}
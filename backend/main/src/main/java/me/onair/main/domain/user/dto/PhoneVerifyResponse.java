package me.onair.main.domain.user.dto;

import lombok.Getter;

@Getter
public class PhoneVerifyResponse {

    private boolean result;

    public PhoneVerifyResponse(boolean result) {
        this.result = result;
    }
}

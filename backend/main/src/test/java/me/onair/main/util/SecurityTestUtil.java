package me.onair.main.util;

import static org.mockito.Mockito.when;

import me.onair.main.domain.user.dto.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTestUtil {

    private static void setUpSecurityContext(CustomUserDetails customUserDetails) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void setUpMockUser(CustomUserDetails customUserDetails, Long userId) {
        when(customUserDetails.getId()).thenReturn(userId);
        SecurityTestUtil.setUpSecurityContext(customUserDetails);
    }
}

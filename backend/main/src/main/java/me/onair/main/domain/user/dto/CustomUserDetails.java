package me.onair.main.domain.user.dto;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import me.onair.main.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 인증을 위해서 DB에서 가져온 사용자 정보를 담는 클래스
// Spring Security에서 사용자 정보를 담는 클래스
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Getter
    private Long id;
    @Getter
    private String profilePath;

    public CustomUserDetails(User user) {
        this.user = user;
        this.id = user.getId();
        this.profilePath = user.getProfilePath();
    }

    // User에 직접 넣을 수 없는 id와 profilePath를 추가한 생성자
    // SecurityContextHolder에 저장할 때 사용
    public CustomUserDetails(User user, Long id, String profilePath) {
        this.user = user;
        this.id = id;
        this.profilePath = profilePath;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getValue()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠김 여부
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호 만료 여부
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부
        return true;
    }
}

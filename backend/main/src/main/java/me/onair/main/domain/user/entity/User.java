package me.onair.main.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.fcm.entity.FcmToken;
import me.onair.main.domain.jwt.entity.RefreshToken;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.user.dto.SignupRequest;
import me.onair.main.domain.user.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    // TODO: 기본 프로필 이미지 경로 수정 필요
    private static final String DEFAULT_PROFILE_PATH = "https://i.namu.wiki/i/MD2QdCJ3W0DvXgUHI8u_dUbdo1y5H_jFBx5w0d8XhTPPbjO8kJeHcvVR6_hOsvHGxhhrKqlGkZvqk744wwYtvtWqyfs3OmXrJ-6B5zLP11S7LglORDjvx3BUS57PCAeRNDfKOCRyYH3VDMdENcMmig.webp";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    // 아이디
    @Column(name = "username", nullable = false, length = 40)
    private String username;

    // 암호화된 비밀번호
    // 자체로는 25글자 이하
    @Column(name = "password", nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true, length = 11)
    private String phoneNumber;

    @Column(name = "profile_path", nullable = false)
    private String profilePath = DEFAULT_PROFILE_PATH;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_USER;

    @OneToOne
    @JoinColumn(name = "fcm_token_id")
    private FcmToken fcmToken;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user")
    private List<Story> stories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Channel> channels = new ArrayList<>();

    @Builder
    public User(String nickname, String username, String password, String phoneNumber, Role role) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // 일반 유저 생성하는 정적 팩토리 메서드
    public static User createNormalUser(SignupRequest request) {
        return User.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.ROLE_USER)
                .build();
    }

    public void updatePicture(String picture) {
        this.profilePath = picture;
    }

    public void deleteFcmToken() {
        this.fcmToken = null;
    }

    public void setFcmToken(FcmToken fcmToken) {
        if (this.fcmToken != null) {
            this.fcmToken.changeUser(null);
        }
        this.fcmToken = fcmToken;
        fcmToken.changeUser(this);
    }

    public void updateRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}

package me.onair.main.domain.user.entity;

import static me.onair.main.domain.user.enums.Role.USER;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.onair.main.domain.channel.entity.Channel;
import me.onair.main.domain.story.entity.Story;
import me.onair.main.domain.user.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nickname", nullable = false, length = 25)
    private String nickname;

    // 아이디
    @Column(name = "username", nullable = false, length = 40)
    private String username;

    // 비밀번호
    @Column(name = "password", nullable = false, length = 25)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "profile_path", nullable = false)
    private String profilePath;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = USER;

    @OneToOne
    @JoinColumn(name = "fcm_token_id")
    private FcmToken fcmToken;

    @OneToOne
    @JoinColumn(name = "refresh_token_id")
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user")
    private List<Story> stories = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Channel> channels = new ArrayList<>();

    private User(String nickname, String username, String password, String phoneNumber, String profilePath) {
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profilePath = profilePath;
    }

    public static User of(String nickname, String username, String password, String phoneNumber, String profilePath) {
        return new User(nickname, username, password, phoneNumber, profilePath);
    }

    public void updatePicture(String picture) {
        this.profilePath = picture;
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }

    public void deleteFcmToken() {
        this.fcmToken = null;
    }

    public void setFcmToken(FcmToken fcmToken) {
        if (this.fcmToken != null) {
            this.fcmToken.changeMobiUser(null);
        }
        this.fcmToken = fcmToken;
        fcmToken.changeMobiUser(this);
    }

}

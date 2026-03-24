package com.campusmarket.backend.domain.member.entity;

import com.campusmarket.backend.domain.member.constant.LoginType;
import com.campusmarket.backend.domain.member.constant.MemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "회원")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "회원PK")
    private Long id;

    @Column(name = "게스트UUID", nullable = false, unique = true, length = 100)
    private String guestUuid;

    @Column(name = "아이디", length = 30)
    private String loginId;

    @Column(name = "비밀번호", length = 255)
    private String password;

    @Column(name = "이메일", length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "로그인유형", nullable = false, length = 30)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(name = "상태", nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "닉네임", length = 20, unique = true)
    private String nickname;

    @Column(name = "프로필이미지URL", length = 500)
    private String profileImageUrl;

    @Column(name = "사물함", length = 100)
    private String lockerName;

    @Column(name = "시간표URL", length = 500)
    private String timetableImageUrl;

    @Column(name = "프로필완료여부", nullable = false)
    private Boolean profileCompleted;


    @Column(name = "생성일")
    private LocalDateTime createdAt;

    @Column(name = "수정일")
    private LocalDateTime updatedAt;

    @Column(name = "탈퇴일")
    private LocalDateTime withdrawnAt;

    @Builder
    private Member(String guestUuid,
                   String loginId,
                   String password,
                   String email,
                   LoginType loginType,
                   MemberStatus status,
                   String nickname,
                   String profileImageUrl,
                   String lockerName,
                   String timetableImageUrl,
                   Boolean profileCompleted
    ){
        this.guestUuid = guestUuid;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.loginType = loginType;
        this.status = status;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.lockerName = lockerName;
        this.timetableImageUrl = timetableImageUrl;
        this.profileCompleted = profileCompleted;
    }

    @PrePersist
    protected void onCreate(){
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = MemberStatus.ACTIVE;
        }

        if (this.profileCompleted == null) {
            this.profileCompleted = false;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void createProfile(
            String nickname,
            String profileImageUrl,
            String lockerName,
            String timetableImageUrl
    ) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.lockerName = lockerName;
        this.timetableImageUrl = timetableImageUrl;
        this.profileCompleted = true;
    }

    public void updateProfile(
            String nickname,
            String profileImageUrl,
            String lockerName,
            String timetableImageUrl
    ) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }

        if (lockerName != null) {
            this.lockerName = lockerName;
        }

        if (timetableImageUrl != null) {
            this.timetableImageUrl = timetableImageUrl;
        }

        this.profileCompleted = isProfileFilled();
    }

    private boolean isProfileFilled() {
        return nickname != null && !nickname.isBlank()
                && profileImageUrl != null && !profileImageUrl.isBlank()
                && lockerName != null && !lockerName.isBlank()
                && timetableImageUrl != null && !timetableImageUrl.isBlank();
    }

}

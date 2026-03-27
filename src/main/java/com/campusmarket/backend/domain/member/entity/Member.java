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

    @Column(name = "사물함건물", length = 50)
    private String lockerBuilding;

    @Column(name = "사물함층", length = 20)
    private String lockerFloor;

    @Column(name = "사물함학과", length = 50)
    private String lockerMajor;

    @Column(name = "사물함그룹")
    private Integer lockerGroup;

    @Column(name = "사물함행")
    private Integer lockerRow;

    @Column(name = "사물함열")
    private Integer lockerCol;

    @Column(name = "시간표URL", length = 500)
    private String timetableImageUrl;

    @Column(name = "시간표데이터", columnDefinition = "TEXT")
    private String timetableData;  // JSON: {"classes": [{"day":"월","start_time":"10:00","end_time":"12:00",...}]}

    @Column(name = "프로필완료여부", nullable = false)
    private Boolean profileCompleted;

    @Column(name = "약관동의완료여부", nullable = false)
    private Boolean termsCompleted;

    @Column(name = "온보딩스킵여부", nullable = false)
    private Boolean onboardingSkipped;

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
                   String timetableData,
                   Boolean profileCompleted,
                   Boolean termsCompleted,
                   Boolean onboardingSkipped
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
        this.timetableData = timetableData;
        this.profileCompleted = profileCompleted;
        this.termsCompleted = termsCompleted;
        this.onboardingSkipped = onboardingSkipped;
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

        if (this.termsCompleted == null) {
            this.termsCompleted = false;
        }

        if (this.onboardingSkipped == null) {
            this.onboardingSkipped = false;
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
            String timetableData
    ) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.lockerName = lockerName;
        this.timetableData = timetableData;
        this.profileCompleted = true;
    }

    public void updateProfile(
            String nickname,
            String profileImageUrl,
            String lockerName,
            String timetableData
    ) {
        if (nickname != null) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
        if (lockerName != null) this.lockerName = lockerName;
        if (timetableData != null) this.timetableData = timetableData;
        this.profileCompleted = isProfileFilled();
    }

    private boolean isProfileFilled() {
        return nickname != null && !nickname.isBlank()
                && profileImageUrl != null && !profileImageUrl.isBlank()
                && lockerName != null && !lockerName.isBlank();
    }

    public void skipOnboarding() {
        this.onboardingSkipped = true;
    }

    public void completeTerms() {
        this.termsCompleted = true;
    }

    public void updateTermsCompleted(Boolean termsCompleted){
        this.termsCompleted = termsCompleted;
    }

    // 사물함 위치 저장/수정
    public void updateLocker(String lockerName, String lockerBuilding, String lockerFloor,
                             String lockerMajor, Integer lockerGroup, Integer lockerRow, Integer lockerCol) {
        this.lockerName = lockerName;
        this.lockerBuilding = lockerBuilding;
        this.lockerFloor = lockerFloor;
        this.lockerMajor = lockerMajor;
        this.lockerGroup = lockerGroup;
        this.lockerRow = lockerRow;
        this.lockerCol = lockerCol;
    }

    // 사물함 해제
    public void deleteLocker() {
        this.lockerName = null;
        this.lockerBuilding = null;
        this.lockerFloor = null;
        this.lockerMajor = null;
        this.lockerGroup = null;
        this.lockerRow = null;
        this.lockerCol = null;
    }

    // 시간표 데이터 저장
    public void updateTimetable(String timetableData) {
        if (timetableData != null) this.timetableData = timetableData;
    }

    // 회원 탈퇴 처리
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
        this.withdrawnAt = java.time.LocalDateTime.now();
    }

    public boolean isWithdrawn() {
        return this.status == MemberStatus.WITHDRAWN;
    }

}

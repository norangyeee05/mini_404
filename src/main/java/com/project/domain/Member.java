package com.project.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_WEB_ADMIN = "WEB_ADMIN";
    public static final String ROLE_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'USER'")
    private String role = ROLE_USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "terms_accepted_at")
    private LocalDateTime termsAcceptedAt;

    @Column(name = "privacy_accepted_at")
    private LocalDateTime privacyAcceptedAt;

    public static Builder builder() { return new Builder(); }

    public static Member create(String loginId, String encodedPassword, String name, String email, String phone) {
        Member member = new Member();
        member.loginId = loginId;
        member.password = encodedPassword;
        member.name = name;
        member.email = email;
        member.phone = phone;
        member.role = ROLE_USER;
        member.status = MemberStatus.ACTIVE;
        return member;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    public void updateProfile(String name, String email, String encodedPassword) {
        this.name = name;
        this.email = email;
        if (encodedPassword != null) this.password = encodedPassword;
    }

    public void changePassword(String encodedPassword) { this.password = encodedPassword; }
    public void changePhone(String phone) { this.phone = phone; }
    public void acceptLegalAgreements(LocalDateTime acceptedAt) {
        this.termsAcceptedAt = acceptedAt;
        this.privacyAcceptedAt = acceptedAt;
    }
    public void changeStatus(MemberStatus status) { this.status = status; }
    public void changeRole(String role) { this.role = role; }
    public void withdraw() { this.status = MemberStatus.WITHDRAWN; }

    public String getPasswordHash() { return password; }

    public static final class Builder {
        private final Member member = new Member();
        public Builder loginId(String value) { member.loginId = value; return this; }
        public Builder password(String value) { member.password = value; return this; }
        public Builder passwordHash(String value) { member.password = value; return this; }
        public Builder name(String value) { member.name = value; return this; }
        public Builder email(String value) { member.email = value; return this; }
        public Builder phone(String value) { member.phone = value; return this; }
        public Builder role(String value) { if (value != null) member.role = value; return this; }
        public Builder status(MemberStatus value) { if (value != null) member.status = value; return this; }
        public Builder createdAt(LocalDateTime value) { member.createdAt = value; return this; }
        public Member build() { return member; }
    }
}

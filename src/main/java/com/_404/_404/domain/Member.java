package com._404._404.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login_id", nullable = false, unique = true, length = 30)
	private String loginId;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(nullable = false, length = 100)
	private String email;

	@Column(length = 20)
	private String phone;

	@Column(nullable = false, length = 20)
	private String role = ROLE_USER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ACTIVE'")
	private MemberStatus status = MemberStatus.ACTIVE;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public Member(String loginId, String passwordHash, String name, String email, String phone,
				  String role, MemberStatus status, LocalDateTime createdAt) {
		this.loginId = loginId;
		this.passwordHash = passwordHash;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.createdAt = createdAt;
		if (role != null) {
			this.role = role;
		}
		if (status != null) {
			this.status = status;
		}
	}

	@PrePersist
	void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now(KST);
		}
		updatedAt = createdAt;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now(KST);
	}

	public void changePassword(String newPasswordHash) {
		this.passwordHash = newPasswordHash;
	}

	public void updateProfile(String name, String email, String phone) {
		this.name = name;
		this.email = email;
		this.phone = phone;
	}

	public void changeStatus(MemberStatus status) {
		this.status = status;
	}
}

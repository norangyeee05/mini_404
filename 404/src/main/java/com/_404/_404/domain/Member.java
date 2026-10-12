package com._404._404.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

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

	@Column(nullable = false, length = 20)
	private String role = ROLE_USER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'ACTIVE'")
	private MemberStatus status = MemberStatus.ACTIVE;

	@Builder
	public Member(String loginId, String passwordHash, String name, String email,
				  String role, MemberStatus status) {
		this.loginId = loginId;
		this.passwordHash = passwordHash;
		this.name = name;
		this.email = email;
		if (role != null) {
			this.role = role;
		}
		if (status != null) {
			this.status = status;
		}
	}

	public void changePassword(String newPasswordHash) {
		this.passwordHash = newPasswordHash;
	}
}

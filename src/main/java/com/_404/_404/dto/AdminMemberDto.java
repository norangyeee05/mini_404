package com._404._404.dto;

import java.util.List;

public final class AdminMemberDto {

	private AdminMemberDto() {
	}

	public record MemberSummary(long total, long active, long blocked, long inactive, long banned) {
	}

	public record MemberRow(Long id, String memberNo, String name, String loginId, String email,
							String status, String statusLabel) {
	}

	public record MemberListResponse(MemberSummary summary, List<MemberRow> members,
									 PageInfo pageInfo, String type, String keyword) {
	}

	public record MemberDetail(Long id, String memberNo, String name, String loginId, String email,
							   String phone, String status, String statusLabel, String role, String grade,
							   String createdAt, String updatedAt) {
	}

	public record MemberUpdateRequest(String name, String email, String phone) {
	}

	public record MemberStatusRequest(com._404._404.domain.MemberStatus status) {
	}

	public record PasswordResetResult(String temporaryPassword) {
	}
}

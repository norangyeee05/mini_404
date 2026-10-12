package com._404._404.controller;

import com._404._404.dto.AdminMemberDto.MemberDetail;
import com._404._404.dto.AdminMemberDto.MemberListResponse;
import com._404._404.dto.AdminMemberDto.MemberStatusRequest;
import com._404._404.dto.AdminMemberDto.MemberUpdateRequest;
import com._404._404.dto.AdminMemberDto.PasswordResetResult;
import com._404._404.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/members")
@RequiredArgsConstructor
public class AdminMemberController {

	private final AdminMemberService adminMemberService;

	@GetMapping
	public MemberListResponse list(@RequestParam(defaultValue = "ALL") String type,
								   @RequestParam(defaultValue = "") String keyword,
								   @RequestParam(defaultValue = "1") int page) {
		return adminMemberService.list(type, keyword, page);
	}

	@GetMapping("/{id}")
	public MemberDetail detail(@PathVariable Long id) {
		return adminMemberService.detail(id);
	}

	@PutMapping("/{id}")
	public MemberDetail update(@PathVariable Long id, @RequestBody MemberUpdateRequest request) {
		return adminMemberService.update(id, request);
	}

	@PatchMapping("/{id}/status")
	public MemberDetail changeStatus(@PathVariable Long id, @RequestBody MemberStatusRequest request) {
		return adminMemberService.changeStatus(id, request.status());
	}

	@PostMapping("/{id}/reset-password")
	public PasswordResetResult resetPassword(@PathVariable Long id) {
		return adminMemberService.resetPassword(id);
	}
}

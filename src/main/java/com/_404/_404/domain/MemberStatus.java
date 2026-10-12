package com._404._404.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

	ACTIVE("활성"),
	BLOCKED("임시차단"),
	INACTIVE("비활성"),
	BANNED("강퇴");

	private final String label;
}

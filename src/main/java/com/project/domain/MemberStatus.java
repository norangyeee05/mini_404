package com.project.domain;

/**
 * 회원 상태.
 * ACTIVE(활성화), BLOCKED(임시차단), BANNED(강퇴·비활성화)는 API 명세서 Enum이고,
 * WITHDRAWN(탈퇴)은 회원 탈퇴 처리를 위해 추가한 값입니다. (관리자 상태 변경 API에서는 지정 불가)
 */
public enum MemberStatus {
    ACTIVE, BLOCKED, BANNED, WITHDRAWN
}

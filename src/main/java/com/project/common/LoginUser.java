package com.project.common;

import java.io.Serializable;

import com.project.domain.Role;

/** 세션에 저장되는 로그인 정보. 회원 계정 또는 별도 관리자 계정의 권한을 보관합니다. */
public record LoginUser(Long id, String name, Role role, boolean adminAccount) implements Serializable {

    public LoginUser(Long id, String name, Role role) {
        this(id, name, role, false);
    }

    /** 일반 회원만 허용 */
    public void requireUser() {
        if (role != Role.USER) {
            throw BusinessException.forbidden("일반 회원만 이용할 수 있습니다.");
        }
    }

    public void requireMemberAccount() {
        if (adminAccount) {
            throw BusinessException.forbidden("회원 계정만 이용할 수 있습니다.");
        }
    }
}

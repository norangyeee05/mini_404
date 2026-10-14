package com.project.common;

import java.io.Serializable;

import com.project.domain.Role;

/** 세션에 저장되는 로그인 정보. role=USER 이면 id는 member.id, ADMIN 이면 admin.id */
public record LoginUser(Long id, String name, Role role) implements Serializable {

    /** 일반 회원만 허용 */
    public void requireUser() {
        if (role != Role.USER) {
            throw BusinessException.forbidden("일반 회원만 이용할 수 있습니다.");
        }
    }
}

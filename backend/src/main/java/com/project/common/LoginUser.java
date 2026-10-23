package com.project.common;

import java.io.Serializable;

import com.project.domain.Role;


public record LoginUser(Long id, String name, Role role, boolean adminAccount) implements Serializable {

    public LoginUser(Long id, String name, Role role) {
        this(id, name, role, false);
    }


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

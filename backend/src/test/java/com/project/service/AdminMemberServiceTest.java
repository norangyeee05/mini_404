package com.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.common.BusinessException;
import com.project.domain.Member;
import com.project.domain.Role;
import com.project.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceTest {
    @Mock MemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks AdminMemberService service;

    @Test
    void changeRoleStoresTheSelectedEnumRole() {
        Member member = member();
        when(memberRepository.findById(7L)).thenReturn(Optional.of(member));

        Member updated = service.changeRole(7L, "WEB_ADMIN");

        assertEquals(Role.WEB_ADMIN, updated.getRole());
    }

    @Test
    void changeRoleRejectsUnsupportedValues() {
        Member member = member();
        when(memberRepository.findById(7L)).thenReturn(Optional.of(member));

        assertThrows(BusinessException.class, () -> service.changeRole(7L, "ROOT"));
        assertEquals(Role.USER, member.getRole());
    }

    private Member member() {
        return Member.builder()
                .loginId("member7")
                .password("encoded")
                .name("테스트 회원")
                .email("member7@example.test")
                .build();
    }
}

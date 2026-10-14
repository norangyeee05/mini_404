package com.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.domain.Member;
import com.project.domain.MemberStatus;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    boolean existsByLoginId(String loginId);
    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findFirstByNameAndEmail(String name, String email);
    Optional<Member> findByLoginIdAndNameAndEmail(String loginId, String name, String email);
    long countByStatus(MemberStatus status);
}

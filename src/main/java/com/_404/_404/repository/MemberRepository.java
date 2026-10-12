package com._404._404.repository;

import com._404._404.domain.Member;
import com._404._404.domain.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);

	Optional<Member> findFirstByNameAndEmail(String name, String email);

	Optional<Member> findByLoginIdAndNameAndEmail(String loginId, String name, String email);

	long countByStatus(MemberStatus status);

	Page<Member> findByNameContainingIgnoreCase(String name, Pageable pageable);

	Page<Member> findByLoginIdContainingIgnoreCase(String loginId, Pageable pageable);

	Page<Member> findByEmailContainingIgnoreCase(String email, Pageable pageable);

	Page<Member> findByNameContainingIgnoreCaseOrLoginIdContainingIgnoreCaseOrEmailContainingIgnoreCase(
			String name, String loginId, String email, Pageable pageable);
}

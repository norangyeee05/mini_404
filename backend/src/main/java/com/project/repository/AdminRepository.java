package com.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.domain.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    boolean existsByLoginId(String loginId);

    Optional<Admin> findByLoginId(String loginId);
}

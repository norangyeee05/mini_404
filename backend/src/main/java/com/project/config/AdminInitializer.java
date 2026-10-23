package com.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.domain.Admin;
import com.project.domain.AdminRole;
import com.project.repository.AdminRepository;


@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String loginId;
    private final String password;
    private final String name;
    private final String email;

    public AdminInitializer(AdminRepository adminRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.login-id}") String loginId,
                            @Value("${app.admin.password}") String password,
                            @Value("${app.admin.name}") String name,
                            @Value("${app.admin.email}") String email) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminRepository.existsByLoginId(loginId)) {
            return;
        }
        adminRepository.save(Admin.create(loginId, passwordEncoder.encode(password), name, email, AdminRole.ADMIN));
        log.info("기본 관리자 계정을 생성했습니다. (loginId={})", loginId);
    }
}

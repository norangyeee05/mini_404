package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.domain.ResourceRequest;

public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long>, JpaSpecificationExecutor<ResourceRequest> {
}

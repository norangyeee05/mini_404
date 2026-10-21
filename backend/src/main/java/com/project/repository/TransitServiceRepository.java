package com.project.repository;

import com.project.domain.OperationStatus;
import com.project.domain.TransitService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitServiceRepository extends JpaRepository<TransitService, Long> {

	long countByStatus(OperationStatus status);
}

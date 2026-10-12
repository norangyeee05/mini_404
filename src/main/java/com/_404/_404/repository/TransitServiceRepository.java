package com._404._404.repository;

import com._404._404.domain.OperationStatus;
import com._404._404.domain.TransitService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitServiceRepository extends JpaRepository<TransitService, Long> {

	long countByStatus(OperationStatus status);
}

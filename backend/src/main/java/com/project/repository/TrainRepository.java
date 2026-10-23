package com.project.repository;

import com.project.domain.Train;
import com.project.domain.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long>, JpaSpecificationExecutor<Train> {

	List<Train> findTop3ByOrderByUpdatedAtDescIdDesc();

	long countByStatusAndActiveTrue(OperationStatus status);
}

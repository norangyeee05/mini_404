package com.project.repository;

import com.project.domain.Qna;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface QnaRepository extends JpaRepository<Qna, Long>, JpaSpecificationExecutor<Qna> {

    List<Qna> findTop3ByOrderByCreatedAtDescIdDesc();

    long countByAnswered(boolean answered);

}

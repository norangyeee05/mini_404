package com.project.repository;

import com.project.domain.Qna;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface QnaRepository extends JpaRepository<Qna, Long>, JpaSpecificationExecutor<Qna> {

    List<Qna> findTop3ByOrderByCreatedAtDescIdDesc();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Qna q set q.viewCount = q.viewCount + 1 where q.id = :id")
    int increaseViewCount(@Param("id") Long id);

    long countByAnswered(boolean answered);

}

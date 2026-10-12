package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.domain.ResourcePost;

public interface ResourcePostRepository extends JpaRepository<ResourcePost, Long>, JpaSpecificationExecutor<ResourcePost> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ResourcePost r set r.viewCount = r.viewCount + 1 where r.id = :id")
    int increaseViewCount(@Param("id") Long id);
}

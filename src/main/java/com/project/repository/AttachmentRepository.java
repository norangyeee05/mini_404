package com.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByTargetTypeAndTargetIdOrderByIdAsc(AttachmentTarget targetType, Long targetId);

    List<Attachment> findByTargetTypeAndTargetIdAndIdIn(AttachmentTarget targetType, Long targetId, List<Long> ids);

    Optional<Attachment> findByIdAndTargetTypeAndTargetId(Long id, AttachmentTarget targetType, Long targetId);
}

package com.project.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.BusinessException;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.dto.ResourceDto.FileInfo;
import com.project.repository.AttachmentRepository;

@RequiredArgsConstructor
@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void saveAll(List<MultipartFile> files, AttachmentTarget target, Long targetId) {
        if (files == null) {
            return;
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            attachmentRepository.save(fileStorageService.store(file, target, targetId));
        }
    }

    @Transactional(readOnly = true)
    public List<FileInfo> list(AttachmentTarget target, Long targetId) {
        return attachmentRepository.findByTargetTypeAndTargetIdOrderByIdAsc(target, targetId).stream()
                .map(a -> new FileInfo(a.getId(), a.getOriginalName()))
                .toList();
    }

    /** 수정 화면에서 선택한 기존 첨부파일만 삭제 (다른 게시글의 파일은 건드리지 않음) */
    @Transactional
    public void deleteSelected(AttachmentTarget target, Long targetId, List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        for (Attachment a : attachmentRepository.findByTargetTypeAndTargetIdAndIdIn(target, targetId, fileIds)) {
            fileStorageService.deleteQuietly(a);
            attachmentRepository.delete(a);
        }
    }

    @Transactional
    public void deleteAll(AttachmentTarget target, Long targetId) {
        for (Attachment a : attachmentRepository.findByTargetTypeAndTargetIdOrderByIdAsc(target, targetId)) {
            fileStorageService.deleteQuietly(a);
            attachmentRepository.delete(a);
        }
    }

    @Transactional(readOnly = true)
    public Attachment get(AttachmentTarget target, Long targetId, Long fileId) {
        return attachmentRepository.findByIdAndTargetTypeAndTargetId(fileId, target, targetId)
                .orElseThrow(() -> BusinessException.notFound("파일을 찾을 수 없습니다."));
    }
}

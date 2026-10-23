package com.project.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.BusinessException;
import com.project.common.PageResponse;
import com.project.common.SearchUtil;
import com.project.domain.Admin;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.domain.ResourcePost;
import com.project.dto.ResourceDto.Detail;
import com.project.dto.ResourceDto.ListItem;
import com.project.dto.ResourceDto.PostForm;
import com.project.repository.AdminRepository;
import com.project.repository.ResourcePostRepository;

import jakarta.persistence.criteria.JoinType;

/** 자료실 (조회·다운로드는 전체 공개, 등록·수정·삭제는 관리자) */
@Service
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourcePostRepository resourceRepository;
    private final AdminRepository adminRepository;
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    public ResourceService(ResourcePostRepository resourceRepository,
                           AdminRepository adminRepository,
                           AttachmentService attachmentService,
                           FileStorageService fileStorageService) {
        this.resourceRepository = resourceRepository;
        this.adminRepository = adminRepository;
        this.attachmentService = attachmentService;
        this.fileStorageService = fileStorageService;
    }

    /** searchType: title(기본) / writer */
    public PageResponse<ListItem> list(String searchType, String keyword, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));
        Page<ListItem> result = resourceRepository.findAll(search(searchType, keyword), pageable)
                .map(this::toListItem);
        return PageResponse.of(result);
    }

    /** 상세 조회 (조회수 +1) */
    @Transactional
    public Detail detail(Long resourceId) {
        if (resourceRepository.increaseViewCount(resourceId) == 0) {
            throw BusinessException.notFound("자료를 찾을 수 없습니다.");
        }
        ResourcePost r = find(resourceId);
        return new Detail(
                r.getId(), r.getTitle(), r.getViewCount(), r.getAdmin().getName(), r.getCreatedAt(),
                r.getContent(), attachmentService.list(AttachmentTarget.RESOURCE, r.getId()));
    }

    /** 관리자 상세 조회는 조회수를 증가시키지 않습니다. */
    public Detail adminDetail(Long resourceId) {
        ResourcePost r = find(resourceId);
        return new Detail(r.getId(), r.getTitle(), r.getViewCount(), r.getAdmin().getName(), r.getCreatedAt(),
                r.getContent(), attachmentService.list(AttachmentTarget.RESOURCE, r.getId()));
    }

    /** 다운로드할 파일 (원본 파일명과 저장 경로) */
    public DownloadFile file(Long resourceId, Long fileId) {
        find(resourceId);
        Attachment a = attachmentService.get(AttachmentTarget.RESOURCE, resourceId, fileId);
        return new DownloadFile(fileStorageService.resolve(a), a.getOriginalName(), a.getContentType());
    }

    @Transactional
    public Long create(Long adminId, PostForm form, List<MultipartFile> files) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> BusinessException.unauthorized("관리자 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        ResourcePost saved = resourceRepository.save(
                ResourcePost.create(form.title().trim(), form.content().trim(), admin));
        attachmentService.saveAll(files, AttachmentTarget.RESOURCE, saved.getId());
        return saved.getId();
    }

    @Transactional
    public Long update(Long resourceId, PostForm form, List<MultipartFile> files) {
        ResourcePost r = find(resourceId);
        r.update(form.title().trim(), form.content().trim());
        attachmentService.deleteSelected(AttachmentTarget.RESOURCE, resourceId, form.deleteFileIds());
        attachmentService.saveAll(files, AttachmentTarget.RESOURCE, resourceId);
        return r.getId();
    }

    @Transactional
    public void delete(Long resourceId) {
        ResourcePost r = find(resourceId);
        attachmentService.deleteAll(AttachmentTarget.RESOURCE, resourceId);
        resourceRepository.delete(r);
    }

    private ResourcePost find(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> BusinessException.notFound("자료를 찾을 수 없습니다."));
    }

    private ListItem toListItem(ResourcePost r) {
        return new ListItem(r.getId(), r.getTitle(), r.getViewCount(), r.getAdmin().getName(), r.getCreatedAt());
    }

    private Specification<ResourcePost> search(String searchType, String keyword) {
        return (root, query, cb) -> {
            if (SearchUtil.isBlank(keyword)) {
                return cb.conjunction();
            }
            String like = SearchUtil.likePattern(keyword);
            if ("writer".equals(searchType)) {
                return cb.like(cb.lower(root.join("admin", JoinType.LEFT).<String>get("name")), like, '\\');
            }
            return cb.like(cb.lower(root.<String>get("title")), like, '\\');
        };
    }

    public record DownloadFile(Path path, String fileName, String contentType) {
    }
}

package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.dto.ResourceDto.FileInfo;
import com.project.common.BusinessException;
import com.project.common.PageResponse;
import com.project.common.SearchUtil;
import com.project.domain.Admin;
import com.project.domain.Notice;
import com.project.dto.NoticeDto.Detail;
import com.project.dto.NoticeDto.ListItem;
import com.project.dto.NoticeDto.NoticeForm;
import com.project.repository.AdminRepository;
import com.project.repository.NoticeRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    /** searchType: title (default) / writer */
    public PageResponse<ListItem> list(String searchType, String keyword, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Direction.DESC, "id"));
        Page<ListItem> result = noticeRepository.findAll(search(searchType, keyword), pageable)
                .map(this::toListItem);
        return PageResponse.of(result);
    }

    public Detail adminDetail(Long noticeId) {
        Notice notice = find(noticeId);
        return new Detail(notice.getId(), notice.getTitle(), notice.getViewCount(), notice.getWriter(),
                notice.getCreatedAt(), notice.getContent(), attachmentService.list(AttachmentTarget.NOTICE, notice.getId()));
    }

    @Transactional
    public Detail detail(Long noticeId) {
        if (noticeRepository.increaseViewCount(noticeId) == 0) {
            throw BusinessException.notFound("공지사항을 찾을 수 없습니다.");
        }
        Notice notice = find(noticeId);
        return new Detail(notice.getId(), notice.getTitle(), notice.getViewCount(), notice.getWriter(),
                notice.getCreatedAt(), notice.getContent(), attachmentService.list(AttachmentTarget.NOTICE, notice.getId()));
    }

    public ResourceService.DownloadFile file(Long noticeId, Long fileId) {
        find(noticeId);
        Attachment attachment = attachmentService.get(AttachmentTarget.NOTICE, noticeId, fileId);
        return new ResourceService.DownloadFile(fileStorageService.resolve(attachment), attachment.getOriginalName(), attachment.getContentType());
    }

    @Transactional
    public Long create(Long adminId, NoticeForm form, List<MultipartFile> files) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> BusinessException.unauthorized("관리자 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        Notice saved = noticeRepository.save(Notice.create(
                form.title().trim(), form.content().trim(), admin.getName()));
        attachmentService.saveAll(files, AttachmentTarget.NOTICE, saved.getId());
        return saved.getId();
    }

    @Transactional
    public Long update(Long noticeId, NoticeForm form, List<MultipartFile> files) {
        Notice notice = find(noticeId);
        notice.update(form.title().trim(), form.content().trim());
        attachmentService.saveAll(files, AttachmentTarget.NOTICE, noticeId);
        return notice.getId();
    }

    @Transactional
    public void delete(Long noticeId) {
        Notice notice = find(noticeId);
        attachmentService.deleteAll(AttachmentTarget.NOTICE, noticeId);
        noticeRepository.delete(notice);
    }

    private Notice find(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> BusinessException.notFound("공지사항을 찾을 수 없습니다."));
    }

    private ListItem toListItem(Notice notice) {
        return new ListItem(notice.getId(), notice.getTitle(), notice.getViewCount(),
                notice.getWriter(), notice.getCreatedAt());
    }

    private Specification<Notice> search(String searchType, String keyword) {
        return (root, query, cb) -> {
            if (SearchUtil.isBlank(keyword)) return cb.conjunction();
            String like = SearchUtil.likePattern(keyword);
            if ("writer".equals(searchType)) {
                return cb.like(cb.lower(root.get("writer")), like, '\\');
            }
            return cb.like(cb.lower(root.get("title")), like, '\\');
        };
    }
}

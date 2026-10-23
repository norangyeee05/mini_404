package com.project.service;

import java.util.ArrayList;
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
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.domain.RequestStatus;
import com.project.domain.ResourceRequest;
import com.project.dto.ResourceDto.PostForm;
import com.project.dto.ResourceRequestDto.Detail;
import com.project.dto.ResourceRequestDto.ListItem;
import com.project.repository.MemberRepository;
import com.project.repository.ResourceRequestRepository;
import com.project.service.ResourceService.DownloadFile;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

/** 자료실 추가 요청 (회원이 등록, 관리자가 처리) */
@Service
@Transactional(readOnly = true)
public class ResourceRequestService {

    private final ResourceRequestRepository requestRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    public ResourceRequestService(ResourceRequestRepository requestRepository,
                                  MemberRepository memberRepository,
                                  AttachmentService attachmentService,
                                  FileStorageService fileStorageService) {
        this.requestRepository = requestRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
        this.fileStorageService = fileStorageService;
    }

    // ---------- 회원 ----------

    @Transactional
    public Long create(Long memberId, PostForm form, List<MultipartFile> files) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.unauthorized("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw BusinessException.forbidden("이용이 제한된 계정입니다.");
        }
        ResourceRequest saved = requestRepository.save(
                ResourceRequest.create(member, form.title().trim(), form.content().trim()));
        attachmentService.saveAll(files, AttachmentTarget.RESOURCE_REQUEST, saved.getId());
        return saved.getId();
    }

    // ---------- 관리자 ----------

    /** searchType: title(기본) / requester, status: 처리 상태 필터(선택) */
    public PageResponse<ListItem> list(String searchType, String keyword, RequestStatus status,
                                                      int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));
        Page<ListItem> result = requestRepository.findAll(search(searchType, keyword, status), pageable)
                .map(r -> new ListItem(
                        r.getId(), r.getTitle(), r.getMember().getName(), r.getStatus(), r.getCreatedAt()));
        return PageResponse.of(result);
    }

    public Detail detail(Long requestId) {
        ResourceRequest r = find(requestId);
        return new Detail(
                r.getId(), r.getTitle(), r.getMember().getName(), r.getStatus(), r.getCreatedAt(),
                r.getContent(), attachmentService.list(AttachmentTarget.RESOURCE_REQUEST, r.getId()));
    }

    public DownloadFile file(Long requestId, Long fileId) {
        find(requestId);
        Attachment a = attachmentService.get(AttachmentTarget.RESOURCE_REQUEST, requestId, fileId);
        return new DownloadFile(fileStorageService.resolve(a), a.getOriginalName(), a.getContentType());
    }

    @Transactional
    public ResourceRequest changeStatus(Long requestId, RequestStatus status) {
        ResourceRequest r = find(requestId);
        r.changeStatus(status);
        return r;
    }

    @Transactional
    public void delete(Long requestId) {
        ResourceRequest r = find(requestId);
        attachmentService.deleteAll(AttachmentTarget.RESOURCE_REQUEST, requestId);
        requestRepository.delete(r);
    }

    private ResourceRequest find(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> BusinessException.notFound("자료실 요청을 찾을 수 없습니다."));
    }

    private Specification<ResourceRequest> search(String searchType, String keyword, RequestStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (!SearchUtil.isBlank(keyword)) {
                String like = SearchUtil.likePattern(keyword);
                if ("requester".equals(searchType)) {
                    predicates.add(cb.like(cb.lower(root.join("member", JoinType.LEFT).<String>get("name")), like, '\\'));
                } else {
                    predicates.add(cb.like(cb.lower(root.<String>get("title")), like, '\\'));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

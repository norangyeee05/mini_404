package com.project.service;

import java.nio.file.Path;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.BusinessException;
import com.project.common.LoginUser;
import com.project.common.PageResponse;
import com.project.common.SearchUtil;
import com.project.domain.Admin;
import com.project.domain.AdminRole;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.domain.ResourcePost;
import com.project.domain.Member;
import com.project.domain.Role;
import com.project.dto.ResourceDto.Detail;
import com.project.dto.ResourceDto.ListItem;
import com.project.dto.ResourceDto.PostForm;
import com.project.repository.AdminRepository;
import com.project.repository.MemberRepository;
import com.project.repository.ResourcePostRepository;

import jakarta.persistence.criteria.JoinType;


@Service
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourcePostRepository resourceRepository;
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    public ResourceService(ResourcePostRepository resourceRepository,
                           AdminRepository adminRepository,
                           MemberRepository memberRepository,
                           AttachmentService attachmentService,
                           FileStorageService fileStorageService) {
        this.resourceRepository = resourceRepository;
        this.adminRepository = adminRepository;
        this.memberRepository = memberRepository;
        this.attachmentService = attachmentService;
        this.fileStorageService = fileStorageService;
    }


    public PageResponse<ListItem> list(String searchType, String keyword, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));
        Page<ListItem> result = resourceRepository.findAll(search(searchType, keyword), pageable)
                .map(this::toListItem);
        return PageResponse.of(result);
    }


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


    public Detail adminDetail(Long resourceId) {
        ResourcePost r = find(resourceId);
        return new Detail(r.getId(), r.getTitle(), r.getViewCount(), r.getAdmin().getName(), r.getCreatedAt(),
                r.getContent(), attachmentService.list(AttachmentTarget.RESOURCE, r.getId()));
    }


    public DownloadFile file(Long resourceId, Long fileId) {
        find(resourceId);
        Attachment a = attachmentService.get(AttachmentTarget.RESOURCE, resourceId, fileId);
        return new DownloadFile(fileStorageService.resolve(a), a.getOriginalName(), a.getContentType());
    }

    @Transactional
    public Long create(LoginUser user, PostForm form, List<MultipartFile> files) {
        Admin admin = resolveAdmin(user);
        ResourcePost saved = resourceRepository.save(
                ResourcePost.create(form.title().trim(), form.content().trim(), admin));
        attachmentService.saveAll(files, AttachmentTarget.RESOURCE, saved.getId());
        return saved.getId();
    }

    private Admin resolveAdmin(LoginUser user) {
        if (user.role() != Role.ADMIN && user.role() != Role.WEB_ADMIN) {
            throw BusinessException.forbidden("자료를 등록할 권한이 없습니다.");
        }
        if (user.adminAccount()) {
            return adminRepository.findById(user.id())
                    .orElseThrow(() -> BusinessException.unauthorized("관리자 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        }
        Member member = memberRepository.findById(user.id())
                .filter(value -> value.getRole() == Role.ADMIN || value.getRole() == Role.WEB_ADMIN)
                .orElseThrow(() -> BusinessException.unauthorized("관리자 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        return adminRepository.findByLoginId(member.getLoginId()).orElseGet(() -> {
            AdminRole role = member.getRole() == Role.ADMIN ? AdminRole.ADMIN : AdminRole.MANAGER;
            return adminRepository.save(Admin.create(member.getLoginId(), member.getPasswordHash(),
                    member.getName(), member.getEmail(), role));
        });
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

    @Transactional
    public int deleteAll(List<Long> resourceIds) {
        Set<Long> uniqueIds = new LinkedHashSet<>(resourceIds);
        List<ResourcePost> selected = resourceRepository.findAllById(uniqueIds);
        if (selected.size() != uniqueIds.size()) {
            throw BusinessException.notFound("선택한 자료 중 찾을 수 없는 항목이 있습니다.");
        }
        for (ResourcePost resource : selected) {
            attachmentService.deleteAll(AttachmentTarget.RESOURCE, resource.getId());
        }
        resourceRepository.deleteAll(selected);
        return selected.size();
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

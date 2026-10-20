package com.project.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.project.common.BusinessException;
import com.project.common.PageResponse;
import com.project.common.SearchUtil;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;
import com.project.domain.InquiryType;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.domain.Qna;
import com.project.domain.QnaStatus;
import com.project.dto.QnaDto.Detail;
import com.project.dto.QnaDto.ListItem;
import com.project.dto.QnaDto.QuestionForm;
import com.project.repository.MemberRepository;
import com.project.repository.QnaRepository;
import com.project.dto.ResourceDto.FileInfo;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {
    private final QnaRepository qnaRepository;
    private final MemberRepository memberRepository;
    private final AttachmentService attachmentService;
    private final FileStorageService fileStorageService;

    public PageResponse<ListItem> list(String searchType, String keyword, InquiryType inquiryType,
                                       QnaStatus status, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Direction.DESC, "id"));
        Specification<Qna> spec = search(searchType, keyword, inquiryType, status);
        return PageResponse.of(qnaRepository.findAll(spec, pageable).map(this::toListItem));
    }

    public PageResponse<ListItem> adminList(String searchType, String keyword, InquiryType inquiryType,
                                            QnaStatus status, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return PageResponse.of(qnaRepository.findAll(search(searchType, keyword, inquiryType, status), pageable)
                .map(this::toListItem));
    }

    public PageResponse<ListItem> myList(Long authorId, String searchType, String keyword,
                                         InquiryType inquiryType, int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Direction.DESC, "id"));
        Specification<Qna> spec = search(searchType, keyword, inquiryType, null)
                .and((root, query, cb) -> cb.equal(root.get("authorId"), authorId));
        return PageResponse.of(qnaRepository.findAll(spec, pageable).map(this::toListItem));
    }

    public Detail detail(Long qnaId) {
        return toDetail(find(qnaId));
    }

    public Detail memberDetail(Long qnaId, Long memberId) {
        Qna qna = find(qnaId);
        if (!qna.isPublic() && !memberId.equals(qna.getAuthorId())) {
            throw BusinessException.forbidden("비공개 문의는 작성자만 확인할 수 있습니다.");
        }
        return toDetail(qna);
    }

    @Transactional
    public Long create(Long authorId, QuestionForm form, List<MultipartFile> files) {
        Member member = memberRepository.findById(authorId)
                .orElseThrow(() -> BusinessException.unauthorized("회원 정보를 찾을 수 없습니다. 다시 로그인해주세요."));
        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw BusinessException.forbidden("이용이 제한된 계정입니다.");
        }
        Qna saved = qnaRepository.save(Qna.create(form.title().trim(), form.content().trim(),
                form.inquiryType(), member.getId(), member.getName(), !Boolean.TRUE.equals(form.isPrivate())));
        attachmentService.saveAll(files, AttachmentTarget.QNA, saved.getId());
        return saved.getId();
    }

    @Transactional
    public Long update(Long qnaId, Long authorId, QuestionForm form, List<MultipartFile> files) {
        Qna qna = find(qnaId);
        requireOwner(qna, authorId);
        qna.update(form.title().trim(), form.content().trim(), form.inquiryType(), !Boolean.TRUE.equals(form.isPrivate()));
        attachmentService.saveAll(files, AttachmentTarget.QNA, qna.getId());
        return qna.getId();
    }

    @Transactional
    public void deleteByOwner(Long qnaId, Long authorId) {
        Qna qna = find(qnaId);
        requireOwner(qna, authorId);
        delete(qna);
    }

    @Transactional
    public void deleteByAdmin(Long qnaId) {
        delete(find(qnaId));
    }

    @Transactional
    public Qna answer(Long qnaId, String answer) {
        Qna qna = find(qnaId);
        if (qna.isAnswered()) {
            throw BusinessException.conflict("이미 답변이 등록된 Q&A입니다.");
        }
        qna.setAnswer(answer.trim());
        return qna;
    }

    @Transactional
    public Qna updateAnswer(Long qnaId, String answer) {
        Qna qna = find(qnaId);
        if (!qna.isAnswered()) throw BusinessException.badRequest("답변이 등록된 문의만 수정할 수 있습니다.");
        qna.setAnswer(answer.trim());
        return qna;
    }

    public ResourceService.DownloadFile file(Long qnaId, Long fileId) {
        find(qnaId);
        Attachment a = attachmentService.get(AttachmentTarget.QNA, qnaId, fileId);
        Path path = fileStorageService.resolve(a);
        return new ResourceService.DownloadFile(path, a.getOriginalName(), a.getContentType());
    }

    public ResourceService.DownloadFile memberFile(Long qnaId, Long fileId, Long memberId) {
        Qna qna = find(qnaId);
        if (!qna.isPublic() && !memberId.equals(qna.getAuthorId())) {
            throw BusinessException.forbidden("비공개 문의 파일은 작성자만 확인할 수 있습니다.");
        }
        Attachment a = attachmentService.get(AttachmentTarget.QNA, qnaId, fileId);
        return new ResourceService.DownloadFile(fileStorageService.resolve(a), a.getOriginalName(), a.getContentType());
    }

    private void delete(Qna qna) {
        attachmentService.deleteAll(AttachmentTarget.QNA, qna.getId());
        qnaRepository.delete(qna);
    }

    private void requireOwner(Qna qna, Long authorId) {
        if (qna.getAuthorId() == null || !qna.getAuthorId().equals(authorId)) {
            throw BusinessException.forbidden("작성자만 이용할 수 있습니다.");
        }
    }

    private Qna find(Long qnaId) {
        return qnaRepository.findById(qnaId)
                .orElseThrow(() -> BusinessException.notFound("Q&A를 찾을 수 없습니다."));
    }

    private ListItem toListItem(Qna qna) {
        return new ListItem(qna.getId(), qna.getTitle(), inquiryType(qna), qna.getWriter(), qna.getViewCount(), !qna.isPublic(), status(qna), qna.getCreatedAt());
    }

    private Detail toDetail(Qna qna) {
        List<FileInfo> files = attachmentService.list(AttachmentTarget.QNA, qna.getId());
        return new Detail(qna.getId(), qna.getTitle(), inquiryType(qna), qna.getWriter(), !qna.isPublic(), status(qna),
                qna.getCreatedAt(), qna.getContent(), files, qna.getAnswer());
    }

    private InquiryType inquiryType(Qna qna) {
        return qna.getInquiryType() == null ? InquiryType.ETC : qna.getInquiryType();
    }

    private QnaStatus status(Qna qna) {
        return qna.isAnswered() ? QnaStatus.ANSWERED : QnaStatus.WAITING;
    }

    private Specification<Qna> search(String searchType, String keyword, InquiryType inquiryType, QnaStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inquiryType != null) predicates.add(cb.equal(root.get("inquiryType"), inquiryType));
            if (status != null) predicates.add(cb.equal(root.get("answered"), status == QnaStatus.ANSWERED));
            if (!SearchUtil.isBlank(keyword)) {
                String like = SearchUtil.likePattern(keyword);
                Predicate title = cb.like(cb.lower(root.get("title")), like, '\\');
                Predicate content = cb.like(cb.lower(root.get("content")), like, '\\');
                Predicate writer = cb.like(cb.lower(root.get("writer")), like, '\\');
                if ("writer".equals(searchType)) predicates.add(writer);
                else if ("title".equals(searchType)) predicates.add(title);
                else if ("content".equals(searchType)) predicates.add(content);
                else predicates.add(cb.or(title, content, writer));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

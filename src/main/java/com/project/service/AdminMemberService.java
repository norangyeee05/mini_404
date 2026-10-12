package com.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.BusinessException;
import com.project.common.PageResponse;
import com.project.common.SearchUtil;
import com.project.domain.Member;
import com.project.domain.MemberStatus;
import com.project.dto.AdminMemberDto.Detail;
import com.project.dto.AdminMemberDto.ListItem;
import com.project.repository.MemberRepository;

import jakarta.persistence.criteria.Predicate;

/** 관리자 회원관리 (목록·상세·상태 변경). 비밀번호는 어떤 응답에도 포함하지 않음 */
@Service
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public AdminMemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /** searchType: name(기본) / loginId / email, status: 상태 필터(선택) */
    public PageResponse<ListItem> list(String searchType, String keyword, MemberStatus status,
                                                  int page, int size) {
        Pageable pageable = PageResponse.pageable(page, size,
                Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by(Sort.Direction.DESC, "id")));
        Page<ListItem> result = memberRepository.findAll(search(searchType, keyword, status), pageable)
                .map(m -> new ListItem(
                        m.getId(), m.getName(), m.getLoginId(), m.getEmail(), m.getStatus(), m.getUpdatedAt()));
        return PageResponse.of(result);
    }

    public Detail detail(Long memberId) {
        Member m = find(memberId);
        return new Detail(m.getId(), m.getName(), m.getLoginId(), m.getEmail(), m.getPhone(),
                m.getStatus(), m.getCreatedAt(), m.getUpdatedAt());
    }

    @Transactional
    public Member changeStatus(Long memberId, MemberStatus status) {
        if (status == MemberStatus.WITHDRAWN) {
            throw BusinessException.badRequest("탈퇴 상태로는 변경할 수 없습니다.");
        }
        Member m = find(memberId);
        if (m.getStatus() == MemberStatus.WITHDRAWN) {
            throw BusinessException.badRequest("탈퇴한 회원의 상태는 변경할 수 없습니다.");
        }
        m.changeStatus(status);
        return m;
    }

    private Member find(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BusinessException.notFound("회원을 찾을 수 없습니다."));
    }

    private Specification<Member> search(String searchType, String keyword, MemberStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (!SearchUtil.isBlank(keyword)) {
                String like = SearchUtil.likePattern(keyword);
                String field = switch (searchType == null ? "" : searchType) {
                    case "loginId" -> "loginId";
                    case "email" -> "email";
                    default -> "name";
                };
                predicates.add(cb.like(cb.lower(root.<String>get(field)), like, '\\'));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

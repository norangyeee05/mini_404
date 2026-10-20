package com.project.controller;

import java.util.Map;

import com.project.common.ApiResponse;
import com.project.common.PageResponse;
import com.project.domain.InquiryType;
import com.project.domain.Qna;
import com.project.domain.QnaStatus;
import com.project.dto.QnaDto.AnswerForm;
import com.project.dto.QnaDto.Detail;
import com.project.dto.QnaDto.ListItem;
import com.project.dto.QnaDto.StatusResult;
import com.project.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/qna")
public class AdminQnaController {
    private final QnaService qnaService;

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InquiryType inquiryType,
            @RequestParam(required = false) QnaStatus status) {
        return ApiResponse.ok(qnaService.adminList(searchType, keyword, inquiryType, status, page, size));
    }

    @GetMapping("/{qnaId}")
    public ApiResponse<Detail> detail(@PathVariable Long qnaId) {
        return ApiResponse.ok(qnaService.detail(qnaId));
    }

    @GetMapping("/{qnaId}/files/{fileId}")
    public ResponseEntity<Resource> file(@PathVariable Long qnaId, @PathVariable Long fileId) {
        return FileHelper.download(qnaService.file(qnaId, fileId));
    }

    @PostMapping("/{qnaId}/answer")
    public ApiResponse<StatusResult> answer(@PathVariable Long qnaId, @Valid @RequestBody AnswerForm form) {
        Qna qna = qnaService.answer(qnaId, form.answer());
        return ApiResponse.ok(new StatusResult(qna.getId(), QnaStatus.ANSWERED), "답변이 등록되었습니다.");
    }

    @PutMapping("/{qnaId}/answer")
    public ApiResponse<StatusResult> updateAnswer(@PathVariable Long qnaId, @Valid @RequestBody AnswerForm form) {
        Qna qna = qnaService.updateAnswer(qnaId, form.answer());
        return ApiResponse.ok(new StatusResult(qna.getId(), QnaStatus.ANSWERED), "답변이 수정되었습니다.");
    }

    @DeleteMapping("/{qnaId}")
    public ApiResponse<Void> delete(@PathVariable Long qnaId) {
        qnaService.deleteByAdmin(qnaId);
        return ApiResponse.ok(null, "Q&A가 삭제되었습니다.");
    }
}

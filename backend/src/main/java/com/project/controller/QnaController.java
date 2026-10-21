package com.project.controller;

import java.util.List;
import java.util.Map;

import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.PageResponse;
import com.project.common.SessionKeys;
import com.project.domain.InquiryType;
import com.project.domain.QnaStatus;
import com.project.dto.QnaDto.Detail;
import com.project.dto.QnaDto.ListItem;
import com.project.dto.QnaDto.QuestionForm;
import com.project.service.QnaService;
import com.project.service.ResourceService.DownloadFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qna")
public class QnaController {
    private final QnaService qnaService;

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InquiryType inquiryType) {
        return ApiResponse.ok(qnaService.list(searchType, keyword, inquiryType, null, page, size));
    }

    @GetMapping("/recent")
    public ApiResponse<PageResponse<ListItem>> recent(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @RequestParam(defaultValue = "3") int size) {
        user.requireUser();
        return ApiResponse.ok(qnaService.list(null, null, null, null, 1, size));
    }

    @GetMapping("/my")
    public ApiResponse<PageResponse<ListItem>> myList(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) InquiryType inquiryType) {
        user.requireUser();
        return ApiResponse.ok(qnaService.myList(user.id(), searchType, keyword, inquiryType, page, size));
    }

    @GetMapping("/{qnaId}")
    public ApiResponse<Detail> detail(@PathVariable Long qnaId, @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user) {
        user.requireUser();
        return ApiResponse.ok(qnaService.memberDetail(qnaId, user.id()));
    }

    @GetMapping("/{qnaId}/files/{fileId}")
    public ResponseEntity<Resource> file(@PathVariable Long qnaId, @PathVariable Long fileId,
                                         @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user) {
        user.requireUser();
        return FileHelper.download(qnaService.memberFile(qnaId, fileId, user.id()));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createJson(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Valid @RequestBody QuestionForm form) {
        user.requireUser();
        return created(qnaService.create(user.id(), form, List.of()));
    }

    @PostMapping(consumes = {"multipart/form-data", "application/x-www-form-urlencoded"})
    public ResponseEntity<ApiResponse<Map<String, Long>>> createMultipart(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Validated @ModelAttribute QuestionForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        user.requireUser();
        return created(qnaService.create(user.id(), form, FileHelper.merge(files, bracketFiles)));
    }

    @PutMapping(value = "/{qnaId}", consumes = "application/json")
    public ApiResponse<Map<String, Long>> updateJson(
            @PathVariable Long qnaId,
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Valid @RequestBody QuestionForm form) {
        user.requireUser();
        return ApiResponse.ok(Map.of("qnaId", qnaService.update(qnaId, user.id(), form, List.of())));
    }

    @PutMapping(value = "/{qnaId}", consumes = {"multipart/form-data", "application/x-www-form-urlencoded"})
    public ApiResponse<Map<String, Long>> updateMultipart(
            @PathVariable Long qnaId,
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Validated @ModelAttribute QuestionForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        user.requireUser();
        return ApiResponse.ok(Map.of("qnaId", qnaService.update(qnaId, user.id(), form, FileHelper.merge(files, bracketFiles))));
    }

    @DeleteMapping("/{qnaId}")
    public ApiResponse<Void> delete(@PathVariable Long qnaId,
                                    @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user) {
        user.requireUser();
        qnaService.deleteByOwner(qnaId, user.id());
        return ApiResponse.ok(null, "Q&A가 삭제되었습니다.");
    }

    private ResponseEntity<ApiResponse<Map<String, Long>>> created(Long qnaId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("qnaId", qnaId), "질문이 등록되었습니다."));
    }
}

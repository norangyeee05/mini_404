package com.project.common;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public record PageResponse<T>(long totalCount, int page, int size, List<T> items) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getTotalElements(), page.getNumber() + 1, page.getSize(), page.getContent());
    }


    public static Pageable pageable(int page, int size, Sort sort) {
        int p = Math.max(page, 1) - 1;
        int s = Math.min(Math.max(size, 1), 100);
        return PageRequest.of(p, s, sort);
    }
}

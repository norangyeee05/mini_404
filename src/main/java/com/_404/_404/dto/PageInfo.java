package com._404._404.dto;

import org.springframework.data.domain.Page;

public record PageInfo(int page, int size, int totalPages, long totalElements,
					   int blockStart, int blockEnd, boolean hasPrev, boolean hasNext) {

	private static final int BLOCK_SIZE = 5;

	public static PageInfo from(Page<?> result) {
		int page = result.getNumber() + 1;
		int totalPages = Math.max(result.getTotalPages(), 1);
		int blockStart = ((page - 1) / BLOCK_SIZE) * BLOCK_SIZE + 1;
		int blockEnd = Math.min(blockStart + BLOCK_SIZE - 1, totalPages);
		return new PageInfo(page, result.getSize(), totalPages, result.getTotalElements(),
				blockStart, blockEnd, page > 1, page < totalPages);
	}
}

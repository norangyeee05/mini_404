package com.project.common;

public final class SearchUtil {

    private SearchUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /** LIKE 검색용 패턴 (소문자, %/_/\ 이스케이프). cb.like(expr, pattern, '\\') 와 함께 사용 */
    public static String likePattern(String keyword) {
        String k = keyword.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + k + "%";
    }
}

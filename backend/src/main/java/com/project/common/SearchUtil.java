package com.project.common;

public final class SearchUtil {

    private SearchUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }


    public static String likePattern(String keyword) {
        String k = keyword.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + k + "%";
    }
}

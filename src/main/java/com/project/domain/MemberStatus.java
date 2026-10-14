package com.project.domain;

public enum MemberStatus {
    ACTIVE("활성"), BLOCKED("임시차단"), INACTIVE("비활성"), BANNED("강퇴"), WITHDRAWN("탈퇴");

    private final String label;

    MemberStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}

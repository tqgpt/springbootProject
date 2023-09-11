package com.chunjae.tqgpt.user.entity;

public enum UserRole {
        MEMBER(0, "일반 회원"),
        MANAGER(1, "총괄 관리자"),
        COLLECTOR(2, "기출문제 수집 담당자"),
        OPERATOR(3, "문제 운영자"),
        CHECKER(4, "문제 검수자");
        private final int code;
        private final String description;
        UserRole(int code, String description) {
            this.code = code;
            this.description = description;
        }
}

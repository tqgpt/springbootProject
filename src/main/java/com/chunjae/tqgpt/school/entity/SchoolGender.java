package com.chunjae.tqgpt.school.entity;

public enum SchoolGender {
        COEDUCATIONAL(0, "남녀 공학"),
        BOYS(1, "남학교"),
        GIRLS(2, "여학교");

        private final int code;
        private final String description;
        SchoolGender(int code, String description) {
            this.code = code;
            this.description = description;
        }
}

package com.hhplus.cleanArchitecture.domain.lecture.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LectureSearchQuery {
    private final LocalDate date;
    private final Long userId;

    private LectureSearchQuery(Builder builder) {
        this.date = builder.date;
        this.userId = builder.userId;
    }

    public static class Builder {
        private LocalDate date;
        private Long userId;

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public LectureSearchQuery build() {
            return new LectureSearchQuery(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}

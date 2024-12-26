package com.hhplus.cleanArchitecture.domain.lecture.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterCommand {
    private final Long userId;
    private final Long lectureId;
    private final Long scheduleId;

    private RegisterCommand(Builder builder) {
        this.userId = builder.userId;
        this.lectureId = builder.lectureId;
        this.scheduleId = builder.scheduleId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private Long lectureId;
        private Long scheduleId;

        Builder() {}

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder lectureId(Long lectureId) {
            this.lectureId = lectureId;
            return this;
        }

        public Builder scheduleId(Long scheduleId) {
            this.scheduleId = scheduleId;
            return this;
        }

        public RegisterCommand build() {
            return new RegisterCommand(this);
        }
    }
}
package com.hhplus.cleanArchitecture.interfaces.lecture.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterRequest {
    private Long scheduleId;
}

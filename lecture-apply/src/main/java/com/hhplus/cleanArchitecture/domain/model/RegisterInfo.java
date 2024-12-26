package com.hhplus.cleanArchitecture.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class RegisterInfo {
    private final Long registrationId;
    private final Long userId;
    private final LocalDate registeredAt;
    private final Long lectureId;
    private final String lectureTitle;
    private final String instructor;
    private final LocalDate lectureDate;

}

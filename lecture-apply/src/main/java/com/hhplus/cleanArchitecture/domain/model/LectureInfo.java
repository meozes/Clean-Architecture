package com.hhplus.cleanArchitecture.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LectureInfo {
    private final Long lectureId;
    private final String title;
    private final String instructor;
    private final LocalDate lectureDate;
    private final int currentCount;
    private final int capacity;
}

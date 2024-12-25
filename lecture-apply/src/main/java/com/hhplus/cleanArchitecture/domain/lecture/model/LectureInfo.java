package com.hhplus.cleanArchitecture.domain.lecture.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LectureInfo {
    private final Long lectureId;
    private final String title;
    private final String instructor;
    private final LocalDate lectureDate;
    private final int currentCount;
    private final int capacity;
    public LectureInfo(Long lectureId, String title, String instructor,
                       LocalDate lectureDate, int currentCount, int capacity) {
        this.lectureId = lectureId;
        this.title = title;
        this.instructor = instructor;
        this.lectureDate = lectureDate;
        this.currentCount = currentCount;
        this.capacity = capacity;
    }
}

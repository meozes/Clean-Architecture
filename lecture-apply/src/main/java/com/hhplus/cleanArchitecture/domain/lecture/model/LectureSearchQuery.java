package com.hhplus.cleanArchitecture.domain.lecture.model;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LectureSearchQuery {
    private final LocalDate date;
    public LectureSearchQuery(LocalDate date) {
        this.date = date;
    }
    public LocalDate getDate() {
        return date;
    }
}

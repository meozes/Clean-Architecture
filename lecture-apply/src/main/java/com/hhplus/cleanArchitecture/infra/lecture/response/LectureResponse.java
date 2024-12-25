package com.hhplus.cleanArchitecture.infra.lecture.response;

import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class LectureResponse {
    private final String lectureId;
    private final String title;
    private final String instructor;
    private final String lectureDate;
    private final int currentCount;
    private final int totalSeats;
    private LectureResponse(LectureInfo info) {
        this.lectureId = String.valueOf(info.getLectureId());
        this.title = info.getTitle();
        this.instructor = info.getInstructor();
        this.lectureDate = info.getLectureDate().format(DateTimeFormatter.ISO_DATE);
        this.currentCount = info.getCurrentCount();
        this.totalSeats = info.getCapacity();
    }
    public static LectureResponse from(LectureInfo info) {
        return new LectureResponse(info);
    }
}

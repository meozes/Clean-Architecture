package com.hhplus.cleanArchitecture.domain.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;

import java.time.LocalDate;
import java.util.List;

public interface ILectureRepository {
    List<Lecture> findLecturesWithSchedule(LocalDate date);
}

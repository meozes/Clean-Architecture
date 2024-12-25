package com.hhplus.cleanArchitecture.domain.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface ILectureRepository {
    List<Lecture> findLecturesWithSchedule(LocalDate date);

    List<Registration> getRegisteredLectures(Long userId);
}

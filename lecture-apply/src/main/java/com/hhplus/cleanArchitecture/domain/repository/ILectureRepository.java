package com.hhplus.cleanArchitecture.domain.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ILectureRepository {
    List<Lecture> findLecturesWithSchedule(LocalDate date);

}

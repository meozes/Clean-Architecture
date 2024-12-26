package com.hhplus.cleanArchitecture.domain.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ILectureRepository {
    List<Lecture> findLecturesWithSchedule(LocalDate date);

    List<Registration> getRegisteredLectures(Long userId);

    Optional<Schedule> findScheduleWithLockById(Long scheduleId);

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    Registration save(Registration registration);

}

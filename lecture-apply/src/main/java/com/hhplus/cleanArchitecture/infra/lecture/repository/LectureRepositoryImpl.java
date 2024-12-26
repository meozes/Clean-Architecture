package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements ILectureRepository {
    private final LectureQueryRepository lectureQueryRepository;
    @Override
    public List<Lecture> findLecturesWithSchedule(LocalDate date) {
        return lectureQueryRepository.findLecturesWithSchedule(date);
    }

    @Override
    public List<Registration> getRegisteredLectures(Long userId) {
        return lectureQueryRepository.getRegisteredLectures(userId);
    }

    @Override
    public Optional<Schedule> findScheduleWithLockById(Long scheduleId) {
        return lectureQueryRepository.findScheduleWithLockById(scheduleId);
    }

    @Override
    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        return lectureQueryRepository.existsByUserIdAndLectureId(userId, lectureId);
    }

    @Override
    public Registration save(Registration registration) {
        return lectureQueryRepository.save(registration);
    }
}

package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}

package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.repository.ILectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements ILectureRepository {
//    private final LectureQueryRepository lectureQueryRepository;

    private final LectureJpaRepository lectureJpaRepository;


    @Override
    public List<Lecture> findLecturesWithSchedule(LocalDate date) {
        return lectureJpaRepository.findLecturesWithSchedule(date);
    }




//    @Override
//    public Registration save(Registration registration) {
//        return lectureQueryRepository.save(registration);
//    }
}

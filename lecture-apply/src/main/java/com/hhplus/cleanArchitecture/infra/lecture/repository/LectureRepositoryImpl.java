package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.repository.ILectureRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.hhplus.cleanArchitecture.domain.entity.QLecture.lecture;
import static com.hhplus.cleanArchitecture.domain.entity.QSchedule.schedule;

import java.time.LocalDate;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements ILectureRepository {

    private final JPAQueryFactory queryFactory;

    public List<Lecture> findLecturesWithSchedule(LocalDate date) {
        return queryFactory
                .selectDistinct(lecture)
                .from(lecture)
                .join(lecture.schedules, schedule).fetchJoin()
                .where(
                        schedule.lectureDate.eq(date),
                        schedule.currentCount.lt(schedule.capacity)
                )
                .fetch();
    }

}

package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureQueryRepository {
    private final EntityManager em;
    public List<Lecture> findLecturesWithSchedule(LocalDate date) {
        return em.createQuery(
                        "SELECT DISTINCT l FROM Lecture l " +
                                "JOIN FETCH l.schedules s " +
                                "WHERE s.lectureDate = :date " +
                                "AND s.currentCount < s.capacity", Lecture.class)
                .setParameter("date", date)
                .getResultList();
    }
}

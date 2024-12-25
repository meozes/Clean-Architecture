package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
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

    public List<Registration> getRegisteredLectures(Long userId) {
        return em.createQuery(
                        "SELECT r FROM Registration r " +
                                "JOIN FETCH r.lecture l " +
                                "JOIN FETCH r.schedule s " +
                                "WHERE r.userId = :userId", Registration.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}

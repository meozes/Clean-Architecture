package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Optional<Schedule> findScheduleWithLockById(Long scheduleId) {
        try {
            Schedule schedule = em.createQuery(
                            "SELECT s FROM Schedule s " +
                                    "WHERE s.id = :scheduleId", Schedule.class)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setParameter("scheduleId", scheduleId)
                    .getSingleResult();
            return Optional.of(schedule);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        Long count = em.createQuery(
                        "SELECT COUNT(r) FROM Registration r " +
                                "WHERE r.userId = :userId " +
                                "AND r.lecture.id = :lectureId", Long.class)
                .setParameter("userId", userId)
                .setParameter("lectureId", lectureId)
                .getSingleResult();
        return count > 0;
    }

    public Registration save(Registration registration) {
        em.persist(registration);
        return registration;
    }
}

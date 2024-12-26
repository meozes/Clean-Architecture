package com.hhplus.cleanArchitecture.infra.lecture.repository;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT DISTINCT l FROM Lecture l " +
            "JOIN FETCH l.schedules s " +
            "WHERE s.lectureDate = :date " +
            "AND s.currentCount < s.capacity")
    List<Lecture> findLecturesWithSchedule(@Param("date") LocalDate date);


}

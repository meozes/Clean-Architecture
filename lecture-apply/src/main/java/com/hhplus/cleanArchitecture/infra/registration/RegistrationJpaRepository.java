package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationJpaRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT r FROM Registration r " +
            "JOIN FETCH r.lecture l " +
            "JOIN FETCH r.schedule s " +
            "WHERE r.userId = :userId")
    List<Registration> getRegisteredLectures(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
            "WHERE r.userId = :userId AND r.lecture.id = :lectureId")
    boolean existsByUserIdAndLectureId(@Param("userId") Long userId,
                                       @Param("lectureId") Long lectureId);
}

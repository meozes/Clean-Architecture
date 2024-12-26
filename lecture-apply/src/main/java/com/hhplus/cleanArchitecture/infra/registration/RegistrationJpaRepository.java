package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationJpaRepository extends JpaRepository<Registration, Long> {

    @Query("SELECT r FROM Registration r " +
            "JOIN FETCH r.lecture l " +
            "JOIN FETCH r.schedule s " +
            "WHERE r.userId = :userId")
    List<Registration> getRegisteredLectures(@Param("userId") Long userId);

    @Query(value = """
            SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END
            FROM registration r
            WHERE r.user_id = :userId 
            AND r.schedule_id = :scheduleId
            """, nativeQuery = true)
    Integer existsByUserIdAndScheduleId(@Param("userId") Long userId,
                                       @Param("scheduleId") Long scheduleId);

//    @Query("SELECT COUNT(r) > 0 FROM Registration r " +
//            "WHERE r.userId = :userId AND r.schedule.id = :schedule")
//    boolean existsByUserIdAndLectureId(@Param("userId") Long userId,
//                                       @Param("schedule") Long schedule);


//    @QueryHints(@QueryHint(name = org.hibernate.annotations.QueryHints.FLUSH_MODE, value = "COMMIT"))
//    boolean existsByUserIdAndScheduleId(Long userId, Long scheduleId);
}

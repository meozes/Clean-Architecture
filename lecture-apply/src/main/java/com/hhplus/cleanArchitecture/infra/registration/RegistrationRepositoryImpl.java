package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import static com.hhplus.cleanArchitecture.domain.entity.QLecture.lecture;
import static com.hhplus.cleanArchitecture.domain.entity.QSchedule.schedule;
import static com.hhplus.cleanArchitecture.domain.entity.QRegistration.registration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RegistrationRepositoryImpl implements IRegistrationRepository {
    private final RegistrationJpaRepository registrationJpaRepository;
    private final JPAQueryFactory queryFactory;


    public List<Registration> getRegisteredLectures(Long userId) {
        return queryFactory
                .selectFrom(registration)
                .join(registration.lecture, lecture).fetchJoin()
                .join(registration.schedule, schedule).fetchJoin()
                .where(registration.userId.eq(userId))
                .fetch();
    }




//    @Override
//    public boolean existsByUserIdAndScheduleId(Long userId, Long scheduleId) {
//        Integer result = registrationJpaRepository.existsByUserIdAndScheduleId(userId, scheduleId);
//        return result == 1;  // MySQL에서 EXISTS는 1 또는 0을 반환
//    }

    public boolean existsByUserIdAndScheduleId(Long userId, Long scheduleId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(registration)
                .where(
                        registration.userId.eq(userId),
                        registration.schedule.id.eq(scheduleId)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    public Registration save(Registration registration) {
        return registrationJpaRepository.save(registration);
    }
}

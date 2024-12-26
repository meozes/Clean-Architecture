package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.hhplus.cleanArchitecture.domain.entity.QLecture.lecture;
import static com.hhplus.cleanArchitecture.domain.entity.QSchedule.schedule;
import static com.hhplus.cleanArchitecture.domain.entity.QRegistration.registration;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegistrationRepositoryImpl implements IRegistrationRepository {
    private final JPAQueryFactory queryFactory;
    private final RegistrationJpaRepository registrationJpaRepository;

    public List<Registration> getRegisteredLectures(Long userId) {
        return queryFactory
                .selectFrom(registration)
                .join(registration.lecture, lecture).fetchJoin()
                .join(registration.schedule, schedule).fetchJoin()
                .where(registration.userId.eq(userId))
                .fetch();
    }

    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(registration)
                .where(
                        registration.userId.eq(userId),
                        registration.lecture.id.eq(lectureId)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    public Registration save(Registration registration) {
        return registrationJpaRepository.save(registration);
    }
}

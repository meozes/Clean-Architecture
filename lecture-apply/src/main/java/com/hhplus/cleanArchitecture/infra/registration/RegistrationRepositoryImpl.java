package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegistrationRepositoryImpl implements IRegistrationRepository {
    private final RegistrationJpaRepository registrationJpaRepository;

    @Override
    public List<Registration> getRegisteredLectures(Long userId) {
        return registrationJpaRepository.getRegisteredLectures(userId);
    }

    @Override
    public boolean existsByUserIdAndLectureId(Long userId, Long lectureId) {
        return registrationJpaRepository.existsByUserIdAndLectureId(userId, lectureId);
    }
}

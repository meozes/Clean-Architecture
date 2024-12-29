package com.hhplus.cleanArchitecture.infra.registration;

import com.hhplus.cleanArchitecture.domain.exception.AlreadyRegisteredException;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistrationValidationService {
    private final IRegistrationRepository registrationRepository;

    public void validateRegistration(Long userId, Long scheduleId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("유저 ID는 0보다 커야 합니다.");
        }
        if (registrationRepository.existsByUserIdAndScheduleId(userId, scheduleId)) {
            throw new AlreadyRegisteredException("이미 신청한 강의입니다.");
        }
    }
}

package com.hhplus.cleanArchitecture.domain.repository;

import com.hhplus.cleanArchitecture.domain.entity.Registration;

import java.util.List;

public interface IRegistrationRepository {
    List<Registration> getRegisteredLectures(Long userId);

    boolean existsByUserIdAndScheduleId(Long userId, Long scheduleId);

    Registration save(Registration registration);
}

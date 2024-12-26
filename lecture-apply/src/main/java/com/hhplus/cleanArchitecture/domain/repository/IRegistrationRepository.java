package com.hhplus.cleanArchitecture.domain.repository;

import com.hhplus.cleanArchitecture.domain.entity.Registration;

import java.util.List;

public interface IRegistrationRepository {
    List<Registration> getRegisteredLectures(Long userId);

    boolean existsByUserIdAndLectureId(Long userId, Long lectureId);

    Registration save(Registration registration);
}

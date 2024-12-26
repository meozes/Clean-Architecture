package com.hhplus.cleanArchitecture.domain.usecase;

import com.hhplus.cleanArchitecture.domain.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRegisteredLectureService {
    private final IRegistrationRepository registrationRepository;
    public List<RegisterInfo> getLectures(LectureSearchQuery query) {
        validateUserId(query.getUserId());
        return registrationRepository.getRegisteredLectures(query.getUserId()).stream()
                .map(registration -> new RegisterInfo(
                        registration.getId(),
                        registration.getUserId(),
                        registration.getRegisteredAt(),
                        registration.getLecture().getId(),
                        registration.getLecture().getTitle(),
                        registration.getLecture().getInstructor(),
                        registration.getSchedule().getLectureDate()
                ))
                .collect(Collectors.toList());
    }

    public void validateUserId(Long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("유저 ID는 0보다 커야 합니다");
        }
    }
}

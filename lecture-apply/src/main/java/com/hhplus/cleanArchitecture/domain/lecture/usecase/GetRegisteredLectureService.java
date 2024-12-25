package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetRegisteredLectureService {
    private final ILectureRepository lectureRepository;
    public List<RegisterInfo> getLectures(LectureSearchQuery query) {
        validateUserId(query.getUserId());
        return lectureRepository.getRegisteredLectures(query.getUserId()).stream()
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

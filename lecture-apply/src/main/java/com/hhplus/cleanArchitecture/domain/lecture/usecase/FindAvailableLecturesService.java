package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindAvailableLecturesService {
    private final ILectureRepository lectureRepository;
    public List<LectureInfo> getLectures(LectureSearchQuery query) {
        validateDate(query.getDate());
        return lectureRepository.findLecturesWithSchedule(query.getDate()).stream()
                .map(lecture -> {
                    Schedule schedule = lecture.getSchedules().stream()
                            .filter(s -> query.getDate().equals(s.getLectureDate()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("강의 스케쥴이 없습니다."));
                    return new LectureInfo(
                            lecture.getId(),
                            lecture.getTitle(),
                            lecture.getInstructor(),
                            schedule.getLectureDate(),
                            schedule.getCurrentCount(),
                            schedule.getCapacity()
                    );
                })
                .collect(Collectors.toList());
    }
    public void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("강의 조회는 현재 이후의 날짜만 가능합니다.");
        }
    }
}

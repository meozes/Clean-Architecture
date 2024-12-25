package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindAvailableLecturesServiceTest {
    @Mock
    private ILectureRepository lectureRepository;
    @InjectMocks
    private FindAvailableLecturesService service;
    @Test
    void 특강_조회_성공() {
        // Given
        LocalDate targetDate = LocalDate.of(2024, 12, 25);

        Lecture lecture1 = createLecture(1L, "2주차 멘토링", "하헌우");
        Schedule schedule1 = createSchedule(1L, lecture1, targetDate, 30, 10);
        lecture1.addSchedule(schedule1);

        Lecture lecture2 = createLecture(2L, "스페셜 큐앤에이", "허재");
        Schedule schedule2 = createSchedule(2L, lecture2, targetDate, 20, 5);
        lecture2.addSchedule(schedule2);
        given(lectureRepository.findLecturesWithSchedule(targetDate))
                .willReturn(List.of(lecture1, lecture2));


        // When
        List<LectureInfo> result = service.getLectures(new LectureSearchQuery(targetDate));


        // Then
        assertThat(result).hasSize(2);

        // case1
        LectureInfo springInfo = result.get(0);
        assertThat(springInfo.getLectureId()).isEqualTo(1L);
        assertThat(springInfo.getTitle()).isEqualTo("2주차 멘토링");
        assertThat(springInfo.getInstructor()).isEqualTo("하헌우");
        assertThat(springInfo.getLectureDate()).isEqualTo(targetDate);
        assertThat(springInfo.getCurrentCount()).isEqualTo(10);
        assertThat(springInfo.getCapacity()).isEqualTo(30);

        // case2
        LectureInfo javaInfo = result.get(1);
        assertThat(javaInfo.getLectureId()).isEqualTo(2L);
        assertThat(javaInfo.getTitle()).isEqualTo("스페셜 큐앤에이");
        assertThat(javaInfo.getInstructor()).isEqualTo("허재");
        assertThat(javaInfo.getLectureDate()).isEqualTo(targetDate);
        assertThat(javaInfo.getCurrentCount()).isEqualTo(5);
        assertThat(javaInfo.getCapacity()).isEqualTo(20);
    }


    @Test
    void 과거_날짜_조회_실패() {
        // Given
        LocalDate pastDate = LocalDate.now().minusDays(1);

        // When & Then
        assertThatThrownBy(() -> service.getLectures(new LectureSearchQuery(pastDate)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("강의 조회는 현재 이후의 날짜만 가능합니다.");
    }

    
    @Test
    void 날짜_형식_오류_실패() {
        // Given
        String invalidDateStr = "2024-13-40"; // 잘못된 날짜 형식

        // When & Then
        assertThatThrownBy(() -> {
            LocalDate invalidDate = LocalDate.parse(invalidDateStr);
            service.getLectures(new LectureSearchQuery(invalidDate));
        }).isInstanceOf(DateTimeParseException.class);
    }


    private Lecture createLecture(Long id, String title, String instructor) {
        Lecture lecture = Lecture.builder()
                .title(title)
                .instructor(instructor)
                .build();
        ReflectionTestUtils.setField(lecture, "id", id);
        return lecture;
    }

    private Schedule createSchedule(Long id, Lecture lecture, LocalDate lectureDate,
                                    int capacity, int currentCount) {
        Schedule schedule = Schedule.builder()
                .lecture(lecture)
                .lectureDate(lectureDate)
                .capacity(capacity)
                .currentCount(currentCount)
                .build();
        ReflectionTestUtils.setField(schedule, "id", id);
        return schedule;
    }
}

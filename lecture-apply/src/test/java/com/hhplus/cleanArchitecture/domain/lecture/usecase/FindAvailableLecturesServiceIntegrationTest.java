package com.hhplus.cleanArchitecture.domain.lecture.usecase;


import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class FindAvailableLecturesServiceIntegrationTest {
    @Autowired
    private FindAvailableLecturesService service;

    @Test
    void 특강_조회_성공() {
        // Given
        LocalDate targetDate = LocalDate.of(2024, 12, 25);

        // When
        List<LectureInfo> result = service.getLectures(new LectureSearchQuery(targetDate));

        // Then
        assertThat(result).hasSize(1);

        // case1
        LectureInfo springInfo = result.get(0);
        assertThat(springInfo.getLectureId()).isEqualTo(1L);
        assertThat(springInfo.getTitle()).isEqualTo("크리스마스 기념 특강");
        assertThat(springInfo.getInstructor()).isEqualTo("허재");
        assertThat(springInfo.getLectureDate()).isEqualTo(targetDate);
        assertThat(springInfo.getCurrentCount()).isEqualTo(1);
        assertThat(springInfo.getCapacity()).isEqualTo(30);

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
}


package com.hhplus.cleanArchitecture.domain.lecture.usecase.registration;

import com.hhplus.cleanArchitecture.domain.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.usecase.GetRegisteredLectureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class GetRegisteredLectureServiceIntegrationTest {

    @Autowired
    private GetRegisteredLectureService getRegisteredLectureService;

    @Test
    void 신청한_특강_조회_성공() {
        // given
        Long userId = 1L;

        // when
        List<RegisterInfo> result = getRegisteredLectureService.getLectures(
                LectureSearchQuery.builder()
                        .userId(userId)
                        .build()
        );

        // then
        assertThat(result).hasSize(2);

        RegisterInfo firstLecture = result.get(0);
        assertThat(firstLecture.getUserId()).isEqualTo(userId);
        assertThat(firstLecture.getLectureId()).isEqualTo(1L);
        assertThat(firstLecture.getLectureTitle()).isEqualTo("크리스마스 기념 특강");
        assertThat(firstLecture.getInstructor()).isEqualTo("허재");
        assertThat(firstLecture.getLectureDate()).isEqualTo(LocalDate.of(2024, 12, 26));

        RegisterInfo secondLecture = result.get(1);
        assertThat(secondLecture.getUserId()).isEqualTo(userId);
        assertThat(secondLecture.getLectureId()).isEqualTo(2L);
        assertThat(secondLecture.getLectureTitle()).isEqualTo("근거가 있는 강의");
        assertThat(secondLecture.getInstructor()).isEqualTo("하헌우");
        assertThat(secondLecture.getLectureDate()).isEqualTo(LocalDate.of(2024, 12, 27));
    }

    @Test
    void userId_유효성_실패() {
        // given
        Long invalidUserId = -1L;

        // when & then
        assertThatThrownBy(() ->
                getRegisteredLectureService.getLectures(
                        LectureSearchQuery.builder()
                                .userId(invalidUserId)
                                .build()
                ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유저 ID는 0보다 커야 합니다");
    }

    @Test
    void 수강_신청_결과_없음_성공() {
        // given
        Long userId = 999L; 

        // when
        List<RegisterInfo> result = getRegisteredLectureService.getLectures(
                LectureSearchQuery.builder()
                        .userId(userId)
                        .build()
        );

        // then
        assertThat(result).isEmpty();
    }
}

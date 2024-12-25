package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetRegisteredLectureServiceTest {

    @Mock
    private ILectureRepository lectureRepository;

    @InjectMocks
    GetRegisteredLectureService getRegisteredLectureService;


    @Test
    void 신청한_특강_조회_성공() {
        // given
        Long userId = 1L;
        LocalDate now = LocalDate.now();

        List<Registration> registrations = Arrays.asList(
                createRegistration(1L, userId, 1L, 1L, now),
                createRegistration(2L, userId, 2L, 2L, now)
        );

        when(lectureRepository.getRegisteredLectures(userId))
                .thenReturn(registrations);

        // when
        List<RegisterInfo> result = getRegisteredLectureService.getLectures(
                LectureSearchQuery.builder()
                        .userId(userId)
                        .build()
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getLectureId()).isEqualTo(1L);
        assertThat(result.get(1).getLectureId()).isEqualTo(2L);
        verify(lectureRepository).getRegisteredLectures(userId);
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
        Long userId = 1L;
        when(lectureRepository.getRegisteredLectures(userId))
                .thenReturn(Collections.emptyList());

        // when
        List<RegisterInfo> result = getRegisteredLectureService.getLectures(
                LectureSearchQuery.builder()
                        .userId(userId)
                        .build()
        );

        // then
        assertThat(result).isEmpty();
        verify(lectureRepository).getRegisteredLectures(userId);
    }

    private Registration createRegistration(
            Long registrationId,
            Long userId,
            Long lectureId,
            Long scheduleId,
            LocalDate date) {
        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .title("강의" + lectureId)
                .instructor("강사" + lectureId)
                .build();

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .lecture(lecture)
                .lectureDate(date)
                .capacity(30)
                .currentCount(1)
                .build();

        return Registration.builder()
                .id(registrationId)
                .userId(userId)
                .registeredAt(date)
                .lecture(lecture)
                .schedule(schedule)
                .build();
    }


}

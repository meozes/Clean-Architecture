package com.hhplus.cleanArchitecture.domain.lecture.usecase.schedule;

import com.hhplus.cleanArchitecture.domain.entity.Lecture;
import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.exception.AlreadyRegisteredException;
import com.hhplus.cleanArchitecture.domain.exception.CapacityExceededException;
import com.hhplus.cleanArchitecture.domain.model.RegisterCommand;
import com.hhplus.cleanArchitecture.domain.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.repository.ILectureRepository;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import com.hhplus.cleanArchitecture.domain.repository.IScheduleRepository;
import com.hhplus.cleanArchitecture.domain.usecase.RegisterLectureService;
import com.hhplus.cleanArchitecture.infra.registration.RegistrationJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class RegisterLectureServiceTest {
    @InjectMocks
    private RegisterLectureService service;
    @Mock
    private IRegistrationRepository registrationRepository;
    @Mock
    private IScheduleRepository scheduleRepository;
    @Mock
    private RegistrationJpaRepository registrationJpaRepository;


    @Test
    void 선착순_30명_신청_성공() throws InterruptedException{ // 40명이 신청해도 30명만 가능

        Long lectureId = 1L;
        Long scheduleId = 1L;
        int capacity = 30;
        int applicants = 40;

        log.info("테스트 시작: 정원 {}명, 신청자 {}명", capacity, applicants);

        Schedule schedule = createSchedule(scheduleId, createLecture(lectureId), LocalDate.now(), capacity, 0);

        when(scheduleRepository.findScheduleWithLockById(scheduleId))
                .thenReturn(Optional.of(schedule));
        when(registrationRepository.existsByUserIdAndScheduleId(any(), any()))
                .thenReturn(false);
        when(registrationJpaRepository.save(any(Registration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExecutorService executorService = Executors.newFixedThreadPool(applicants);
        CountDownLatch latch = new CountDownLatch(applicants);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        log.info("동시 신청 시작...");

        // when
        for (int i = 0; i < applicants; i++) {
            final int userId = i + 1;
            executorService.submit(() -> {
                try {
                    service.register(RegisterCommand.builder()
                            .userId((long) userId)
                            .scheduleId(scheduleId)
                            .build());
                    int currentSuccess = successCount.incrementAndGet();
                    log.info("사용자 {} 신청 성공. 현재 신청 인원: {}", userId, currentSuccess);
                } catch (CapacityExceededException e) {
                    int currentFail = failCount.incrementAndGet();
                    log.warn("사용자 {} 신청 실패 (정원 초과). 현재 실패 수: {}", userId, currentFail);
                } catch (Exception e) {
                    int currentFail = failCount.incrementAndGet();
                    log.error("사용자 {} 신청 중 예외 발생: {}. 현재 실패 수: {}", userId, e.getMessage(), currentFail);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        log.info("테스트 완료. 총 신청 성공: {}, 실패: {}, 최종 인원: {}",
                successCount.get(), failCount.get(), schedule.getCurrentCount());
        assertThat(successCount.get()).isEqualTo(capacity);
        assertThat(schedule.getCurrentCount()).isEqualTo(capacity);
    }

    @Test
    void 정원_초과_신청_실패() {
        // given
        Long lectureId = 1L;
        Long scheduleId = 1L;
        Long userId = 31L;

        Schedule schedule = createSchedule(scheduleId, createLecture(lectureId), LocalDate.now(), 30, 30);

        when(scheduleRepository.findScheduleWithLockById(scheduleId))
                .thenReturn(Optional.of(schedule));
        when(registrationRepository.existsByUserIdAndScheduleId(userId, scheduleId))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                service.register(RegisterCommand.builder()
                        .userId(userId)
                        .scheduleId(scheduleId)
                        .build())
        ).isInstanceOf(CapacityExceededException.class)
                .hasMessage("정원이 초과되었습니다.");

    }

    @Test
    void 마지막_한명_신청_성공() throws InterruptedException {
        // given
        Long lectureId = 1L;
        Long scheduleId = 1L;
        int lastApplicants = 10;  // 마지막 자리에 동시 신청하는 인원
        Long firstUserId = 1L;    // 가장 빠른 유저 ID

        Schedule schedule = createSchedule(scheduleId, createLecture(lectureId), LocalDate.now(), 30, 29);

        when(scheduleRepository.findScheduleWithLockById(scheduleId))
                .thenReturn(Optional.of(schedule));
        when(registrationRepository.existsByUserIdAndScheduleId(any(), any()))
                .thenReturn(false);
        when(registrationJpaRepository.save(any(Registration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        log.info("테스트 시작: 마지막 1자리에 {}명 동시 신청", lastApplicants);

        ExecutorService executorService = Executors.newFixedThreadPool(lastApplicants);
        CountDownLatch readyLatch = new CountDownLatch(lastApplicants); // 모든 스레드가 준비될 때까지 대기
        CountDownLatch startLatch = new CountDownLatch(1); // 시작 신호를 기다림
        CountDownLatch doneLatch = new CountDownLatch(lastApplicants); // 모든 작업 완료를 기다림

        AtomicReference<Long> successUserId = new AtomicReference<>(null);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < lastApplicants; i++) {
            final Long userId = firstUserId + i;
            executorService.submit(() -> {
                try {
                    readyLatch.countDown(); // 스레드 준비 완료
                    startLatch.await(); // 시작 신호를 기다림

                    RegisterInfo result = service.register(RegisterCommand.builder()
                            .userId(userId)
                            .scheduleId(scheduleId)
                            .build());
                    successUserId.set(result.getUserId());
                    log.info("사용자 {} 신청 성공", userId);
                } catch (CapacityExceededException e) {
                    int currentFail = failCount.incrementAndGet();
                    log.warn("사용자 {} 신청 실패 (정원 초과). 현재 실패 수: {}", userId, currentFail);
                } catch (Exception e) {
                    int currentFail = failCount.incrementAndGet();
                    log.error("사용자 {} 신청 중 예외 발생: {}. 현재 실패 수: {}", userId, e.getMessage(), currentFail);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(); // 모든 스레드가 준비될 때까지 대기
        startLatch.countDown(); // 모든 스레드에게 동시에 시작 신호를 보냄
        doneLatch.await(); // 모든 작업이 완료될 때까지 대기
        executorService.shutdown();

        // then
        log.info("테스트 완료. 성공한 사용자: {}, 실패 수: {}, 최종 인원: {}",
                successUserId.get(), failCount.get(), schedule.getCurrentCount());

        assertThat(schedule.getCurrentCount()).isEqualTo(30);
        assertThat(failCount.get()).isEqualTo(lastApplicants - 1);  // 나머지는 모두 실패
    }

    @Test
    void 동일_유저_1번만_신청_성공() throws InterruptedException {
        // given
        Long lectureId = 1L;
        Long scheduleId = 1L;
        Long userId = 1L;
        int repeatCount = 5;  // 동시 신청 횟수

        Schedule schedule = createSchedule(scheduleId, createLecture(lectureId), LocalDate.now(), 30, 0);

        AtomicBoolean firstAttempt = new AtomicBoolean(true);
        when(registrationRepository.existsByUserIdAndScheduleId(userId, scheduleId))
                .thenAnswer(invocation -> !firstAttempt.compareAndSet(true, false));  // 첫 시도만 false 반환
        when(scheduleRepository.findScheduleWithLockById(scheduleId))
                .thenReturn(Optional.of(schedule));
        when(registrationJpaRepository.save(any(Registration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        log.info("테스트 시작: 사용자 {}가 {}번 중복 신청", userId, repeatCount);

        ExecutorService executorService = Executors.newFixedThreadPool(repeatCount);
        CountDownLatch latch = new CountDownLatch(repeatCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < repeatCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    service.register(RegisterCommand.builder()
                            .userId(userId)
                            .scheduleId(scheduleId)
                            .build());
                    int currentSuccess = successCount.incrementAndGet();
                    log.info("사용자 {} {}번째 시도 성공. 현재 성공 수: {}", userId, finalI + 1, currentSuccess);
                } catch (AlreadyRegisteredException e) {
                    int currentFail = failCount.incrementAndGet();
                    log.warn("사용자 {} 신청 실패 (중복 신청). 현재 실패 수: {}", userId, currentFail);
                } catch (Exception e) {
                    int currentFail = failCount.incrementAndGet();
                    log.error("사용자 {} 신청 중 예외 발생: {}. 현재 실패 수: {}", userId, e.getMessage(), currentFail);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        log.info("테스트 완료. 성공 수: {}, 실패 수: {}", successCount.get(), failCount.get());
        assertThat(successCount.get()).isEqualTo(1);  // 한 번만 성공
        assertThat(failCount.get()).isEqualTo(repeatCount - 1);  // 나머지는 모두 실패
        assertThat(schedule.getCurrentCount()).isEqualTo(1);  // 현재 인원도 1명
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

    private Lecture createLecture(Long id) {
        Lecture lecture = Lecture.builder()
                .title("강의")
                .instructor("강사")
                .schedules(new ArrayList<>())  // schedules 초기화
                .build();
        ReflectionTestUtils.setField(lecture, "id", id);
        return lecture;
    }
}

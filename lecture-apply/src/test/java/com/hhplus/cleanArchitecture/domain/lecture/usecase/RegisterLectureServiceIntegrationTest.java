package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.exception.AlreadyRegisteredException;
import com.hhplus.cleanArchitecture.domain.exception.CapacityExceededException;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterCommand;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@Transactional
public class RegisterLectureServiceIntegrationTest {
    @Autowired
    private RegisterLectureService registerLectureService;

    @Autowired
    private ILectureRepository lectureRepository;

    @Test
    void 선착순_30명_신청_성공() throws InterruptedException {
        // given
        Long lectureId = 1L;
        Long scheduleId = 1L;
        int applicants = 40;

        ExecutorService executorService = Executors.newFixedThreadPool(applicants);
        CountDownLatch latch = new CountDownLatch(applicants);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        log.info("동시 신청 시작... (신청자: {}명)", applicants);

        // when
        for (int i = 0; i < applicants; i++) {
            final int userId = i + 1;
            executorService.submit(() -> {
                try {
                    registerLectureService.register(RegisterCommand.builder()
                            .userId((long) userId)
                            .lectureId(lectureId)
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
        Schedule schedule = lectureRepository.findScheduleWithLockById(scheduleId).orElseThrow();
        log.info("테스트 완료. 총 신청 성공: {}, 실패: {}, 최종 인원: {}",
                successCount.get(), failCount.get(), schedule.getCurrentCount());

        assertThat(successCount.get()).isEqualTo(30);
        assertThat(schedule.getCurrentCount()).isEqualTo(30);
    }

    @Test
    void 마지막_한명_신청_성공() throws InterruptedException {
        // given
        Long lectureId = 1L;
        Long scheduleId = 2L; // 현재 인원이 29명인 스케줄
        int lastApplicants = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(lastApplicants);
        CountDownLatch latch = new CountDownLatch(lastApplicants);
        AtomicReference<Long> successUserId = new AtomicReference<>(null);
        AtomicInteger failCount = new AtomicInteger(0);

        log.info("테스트 시작: 마지막 1자리에 {}명 동시 신청", lastApplicants);

        // when
        for (int i = 0; i < lastApplicants; i++) {
            final Long userId = (long) (i + 1);
            executorService.submit(() -> {
                try {
                    RegisterInfo result = registerLectureService.register(RegisterCommand.builder()
                            .userId(userId)
                            .lectureId(lectureId)
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
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Schedule schedule = lectureRepository.findScheduleWithLockById(scheduleId).orElseThrow();
        log.info("테스트 완료. 성공한 사용자: {}, 실패 수: {}, 최종 인원: {}",
                successUserId.get(), failCount.get(), schedule.getCurrentCount());

        assertThat(schedule.getCurrentCount()).isEqualTo(30);
        assertThat(successUserId.get()).isNotNull();
        assertThat(failCount.get()).isEqualTo(lastApplicants - 1);
    }


    @Test
    void 동일_유저_중복_신청_실패() throws InterruptedException {
        // given
        Long lectureId = 1L;
        Long scheduleId = 1L;
        Long userId = 1L;
        int repeatCount = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(repeatCount);
        CountDownLatch latch = new CountDownLatch(repeatCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        log.info("테스트 시작: 사용자 {}가 {}번 중복 신청", userId, repeatCount);

        // when
        for (int i = 0; i < repeatCount; i++) {
            executorService.submit(() -> {
                try {
                    registerLectureService.register(RegisterCommand.builder()
                            .userId(userId)
                            .lectureId(lectureId)
                            .scheduleId(scheduleId)
                            .build());
                    int currentSuccess = successCount.incrementAndGet();
                    log.info("사용자 {} 신청 성공. 현재 성공 수: {}", userId, currentSuccess);
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
        Schedule schedule = lectureRepository.findScheduleWithLockById(scheduleId).orElseThrow();
        log.info("테스트 완료. 성공 수: {}, 실패 수: {}, 최종 인원: {}",
                successCount.get(), failCount.get(), schedule.getCurrentCount());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(repeatCount - 1);
        assertThat(schedule.getCurrentCount()).isEqualTo(1);
    }
}

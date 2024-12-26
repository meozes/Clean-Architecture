package com.hhplus.cleanArchitecture.domain.lecture.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.exception.AlreadyRegisteredException;
import com.hhplus.cleanArchitecture.domain.exception.CapacityExceededException;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterCommand;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.lecture.repository.ILectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class RegisterLectureService {
    private final ILectureRepository lectureRepository;

    @Transactional
    public RegisterInfo register(RegisterCommand command) {
        validateUserId(command.getUserId());
        validateDuplicateRegistration(command.getUserId(), command.getLectureId());

        Schedule schedule = lectureRepository.findScheduleWithLockById(command.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스케줄이 존재하지 않습니다."));

        if (schedule.isCapacityFull()) {
            throw new CapacityExceededException("정원이 초과되었습니다.");
        }

        schedule.increaseCurrentCount();

        Registration registration = Registration.builder()
                .userId(command.getUserId())
                .registeredAt(LocalDate.now())
                .lecture(schedule.getLecture())
                .schedule(schedule)
                .build();

        Registration savedRegistration = lectureRepository.save(registration);

        return new RegisterInfo(
                savedRegistration.getId(),
                savedRegistration.getUserId(),
                savedRegistration.getRegisteredAt(),
                schedule.getLecture().getId(),
                schedule.getLecture().getTitle(),
                schedule.getLecture().getInstructor(),
                schedule.getLectureDate()
        );
    }

    private void validateUserId(Long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("유저 ID는 0보다 커야 합니다.");
        }
    }

    private void validateDuplicateRegistration(Long userId, Long lectureId) {
        if (lectureRepository.existsByUserIdAndLectureId(userId, lectureId)) {
            throw new AlreadyRegisteredException("이미 신청한 강의입니다.");
        }
    }

}

package com.hhplus.cleanArchitecture.domain.usecase;

import com.hhplus.cleanArchitecture.domain.entity.Registration;
import com.hhplus.cleanArchitecture.domain.entity.Schedule;
import com.hhplus.cleanArchitecture.domain.exception.AlreadyRegisteredException;
import com.hhplus.cleanArchitecture.domain.exception.CapacityExceededException;
import com.hhplus.cleanArchitecture.domain.model.RegisterCommand;
import com.hhplus.cleanArchitecture.domain.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.repository.IRegistrationRepository;
import com.hhplus.cleanArchitecture.domain.repository.IScheduleRepository;
import com.hhplus.cleanArchitecture.infra.registration.RegistrationJpaRepository;
import com.hhplus.cleanArchitecture.infra.registration.RegistrationValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RegisterLectureService {
    private final IRegistrationRepository registrationRepository;
    private final IScheduleRepository scheduleRepository;
    private final RegistrationJpaRepository registrationJpaRepository;
    private final RegistrationValidationService validationService;

    @Transactional
    public RegisterInfo register(RegisterCommand command) {

        validationService.validateRegistration(command.getUserId(), command.getScheduleId());


        Schedule schedule = scheduleRepository.findScheduleWithLockById(command.getScheduleId())
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

        Registration savedRegistration = registrationJpaRepository.save(registration);

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

}

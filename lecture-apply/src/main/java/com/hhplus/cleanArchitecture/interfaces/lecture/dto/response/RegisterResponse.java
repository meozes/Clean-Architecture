package com.hhplus.cleanArchitecture.interfaces.lecture.dto.response;

import com.hhplus.cleanArchitecture.domain.model.RegisterInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;


@Getter
@AllArgsConstructor
public class RegisterResponse {
    private final String registrationId;
    private final String userId;
    private final String registeredAt;
    private final String lectureId;
    private final String lectureTitle;
    private final String instructor;
    private final String lectureDate;

    private RegisterResponse(RegisterInfo info) {
        this.registrationId = String.valueOf(info.getRegistrationId());
        this.userId = String.valueOf(info.getUserId());
        this.registeredAt = String.valueOf(info.getRegisteredAt());
        this.lectureId = String.valueOf(info.getLectureId());
        this.lectureTitle = info.getLectureTitle();
        this.instructor = info.getInstructor();
        this.lectureDate = info.getLectureDate().format(DateTimeFormatter.ISO_DATE);
    }

    public static RegisterResponse from(RegisterInfo info) {
        return new RegisterResponse(info);
    }
}

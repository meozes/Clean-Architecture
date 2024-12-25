package com.hhplus.cleanArchitecture.interfaces.lecture.controller;

import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.model.RegisterInfo;
import com.hhplus.cleanArchitecture.domain.lecture.usecase.FindAvailableLecturesService;
import com.hhplus.cleanArchitecture.domain.lecture.usecase.GetRegisteredLectureService;
import com.hhplus.cleanArchitecture.interfaces.lecture.dto.response.LectureResponse;
import com.hhplus.cleanArchitecture.interfaces.common.ApiResponse;
import com.hhplus.cleanArchitecture.interfaces.lecture.dto.response.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lectures")
public class LectureController {
    private static final Logger log = LoggerFactory.getLogger(LectureController.class);
    private FindAvailableLecturesService findAvailableLecturesService;
    private GetRegisteredLectureService getRegisteredLectureService;

    /**
     * 신청 가능 강의 목록 조회
     */
    @GetMapping("/available")
    public ApiResponse<List<LectureResponse>> available(
            @RequestParam(name = "date") LocalDate date
    ) {
        List<LectureInfo> lectures = findAvailableLecturesService.getLectures(
                LectureSearchQuery.builder()
                        .date(date)
                        .build()
        );
        return ApiResponse.ok(
                lectures.stream()
                        .map(LectureResponse::from)
                        .collect(Collectors.toList())
        );
    }


    /**
     * 신청 완료 강의 목록 조회
     */
    @GetMapping("/{userId}/registered")
    public ApiResponse<List<RegisterResponse>> registered(
            @PathVariable Long userId
    ) {
        List<RegisterInfo> registerInfos = getRegisteredLectureService.getLectures(
                LectureSearchQuery.builder()
                        .userId(userId)
                        .build()
        );
        return ApiResponse.ok(
                registerInfos.stream()
                        .map(RegisterResponse::from)
                        .collect(Collectors.toList())
        );
    }

}

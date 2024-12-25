package com.hhplus.cleanArchitecture.interfaces.lecture.controller;

import com.hhplus.cleanArchitecture.domain.lecture.model.LectureInfo;
import com.hhplus.cleanArchitecture.domain.lecture.model.LectureSearchQuery;
import com.hhplus.cleanArchitecture.domain.lecture.usecase.FindAvailableLecturesService;
import com.hhplus.cleanArchitecture.interfaces.lecture.dto.response.LectureResponse;
import com.hhplus.cleanArchitecture.interfaces.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lectures")
public class LectureController {
    private static final Logger log = LoggerFactory.getLogger(LectureController.class);
    private FindAvailableLecturesService findAvailableLecturesService;
    /**
     * 신청 가능 강의 목록 조회
     */
    @GetMapping("/available")
    public ApiResponse<List<LectureResponse>> available(
            @RequestParam(name = "date") LocalDate date
    ) {
        List<LectureInfo> lectures = findAvailableLecturesService.getLectures(new LectureSearchQuery(date));
        return ApiResponse.ok(
                lectures.stream()
                        .map(LectureResponse::from)
                        .collect(Collectors.toList())
        );
    }

}

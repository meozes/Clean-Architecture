package com.hhplus.cleanArchitecture.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Schedule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalDate lectureDate;

    @Builder.Default
    private int capacity = 30;

    @Builder.Default
    private int currentCount = 0;
    protected void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }
    public boolean isAvailableFor(LocalDate date) {
        return lectureDate.equals(date) && currentCount < capacity;
    }
}

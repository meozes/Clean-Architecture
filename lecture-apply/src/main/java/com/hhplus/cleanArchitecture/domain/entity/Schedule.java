package com.hhplus.cleanArchitecture.domain.entity;

import com.hhplus.cleanArchitecture.domain.exception.CapacityExceededException;
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

    public boolean isCapacityFull() {
        return this.currentCount >= this.capacity;
    }

    public void increaseCurrentCount() {
        if (isCapacityFull()) {
            throw new CapacityExceededException("정원이 초과되었습니다.");
        }
        this.currentCount++;
    }
}

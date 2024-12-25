package com.hhplus.cleanArchitecture.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lecture extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String instructor;

    @Builder.Default
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();
    public void addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setLecture(this);
    }
    public boolean hasAvailableScheduleFor(LocalDate date) {
        return schedules.stream()
                .anyMatch(schedule -> schedule.isAvailableFor(date));
    }
}

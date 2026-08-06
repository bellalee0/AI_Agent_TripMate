package com.example.tripmate.common.entity;

import com.example.tripmate.common.enums.ScheduleCandidatePreference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_candidates",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_group_user_date",
            columnNames = {"group_room_id", "user_id", "date"}
        )
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleCandidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_room_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleCandidatePreference preference;

    public ScheduleCandidate(Group group, User user, LocalDate date, ScheduleCandidatePreference preference) {
        this.group = group;
        this.user = user;
        this.date = date;
        this.preference = preference;
    }
}

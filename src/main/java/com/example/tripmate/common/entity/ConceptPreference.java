package com.example.tripmate.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concept_preferences",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_group_user_concept",
            columnNames = {"group_room_id", "user_id", "concept_keyword_id"}
        )
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConceptPreference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_room_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_keyword_id", nullable = false)
    private ConceptKeyword keyword;

    private boolean prefer;

    public ConceptPreference(Group group, User user, ConceptKeyword keyword, boolean prefer) {
        this.group = group;
        this.user = user;
        this.keyword = keyword;
        this.prefer = prefer;
    }
}

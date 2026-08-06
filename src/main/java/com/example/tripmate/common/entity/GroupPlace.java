package com.example.tripmate.common.entity;

import com.example.tripmate.common.enums.GroupPlaceSource;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_room_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private String address;

    private Double latitude;

    private Double longitude;

    private boolean confirmed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupPlaceSource source;

    public GroupPlace(Group group, String name, String address, Double latitude, Double longitude, boolean confirmed, GroupPlaceSource source) {
        this.group = group;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.confirmed = confirmed;
        this.source = source;
    }
}

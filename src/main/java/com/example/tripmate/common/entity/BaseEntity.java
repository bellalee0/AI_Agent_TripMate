package com.example.tripmate.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "is_deleted", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean deleted = false;

    @Column
    private LocalDateTime deletedDatetime;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    @LastModifiedDate
    private LocalDateTime modifiedDatetime;

    public void delete() {
        this.deleted = true;
        this.deletedDatetime = LocalDateTime.now();
    }
}

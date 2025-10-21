package com.grabit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditDetails<U> {
    @CreatedBy
    @Column(name = "created_by", nullable = false,updatable = false)
    private U createdBy;

    @CreatedDate
    @Column(name = "created_ts", nullable = false,updatable = false)
    private LocalDateTime createdTs;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private U updatedBy;

    @LastModifiedDate
    @Column(name = "updated_ts", nullable = false)
    private LocalDateTime updatedTs;
}

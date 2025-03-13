package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @CreatedBy
    @Column(name = "last_modifiyed_by")
    private String lastModifiyedBy;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate = Instant.now();

    @LastModifiedDate
    @Column(name = "last_modifiyed_date")
    private Instant lastModifiyedDate = Instant.now();


}

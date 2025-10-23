package com.apluslife.domain.announcement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공시자료 엔티티
 * aplus_gongsi 테이블 매핑
 * SQL: SELECT idx, rdate, title, udate FROM aplus_gongsi
 */
@Entity
@Table(name = "aplus_gongsi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(name = "rdate", nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(name = "udate", nullable = false)
    private LocalDateTime uDate;

    @PrePersist
    protected void onCreate() {
        rDate = LocalDateTime.now();
        uDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        uDate = LocalDateTime.now();
    }
}

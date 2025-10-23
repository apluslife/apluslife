package com.apluslife.domain.announcement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공시자료 엔티티
 * 공시자료, 라이프뉴스, 부고알림 등 공지사항 관리
 */
@Entity
@Table(name = "aplus_공시자료")
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

    @Column(length = 255, nullable = false)
    private String fileName;

    @Column(length = 500, nullable = false)
    private String filePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
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

package com.apluslife.domain.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 라이프뉴스 엔티티
 * 뉴스, 업계 소식, 이벤트 등 뉴스 관련 정보 관리
 */
@Entity
@Table(name = "aplus_라이프뉴스")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 500)
    private String summary;

    @Column(length = 255)
    private String thumbnail;

    @Column(length = 50, nullable = false)
    private String category;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isNotice = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime rDate;

    @Column(nullable = false)
    private LocalDateTime uDate;

    @PrePersist
    protected void onCreate() {
        rDate = LocalDateTime.now();
        uDate = LocalDateTime.now();
        viewCount = 0;
        isNotice = false;
    }

    @PreUpdate
    protected void onUpdate() {
        uDate = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void toggleNotice() {
        this.isNotice = !this.isNotice;
    }
}

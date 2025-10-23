package com.apluslife.domain.lifenews.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 라이프뉴스 엔티티
 * MyBatis를 사용하는 일반 POJO 클래스
 * aplus_라이프뉴스 테이블 매핑
 * SQL: SELECT idx, title, content, rDate, uDate FROM aplus_라이프뉴스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifeNews {

    private Integer idx;                // 고유 ID
    private String title;               // 제목
    private String content;             // 내용
    private LocalDateTime rDate;        // 등록일
    private LocalDateTime uDate;        // 수정일
}

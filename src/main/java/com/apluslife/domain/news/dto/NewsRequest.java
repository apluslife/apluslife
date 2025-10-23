package com.apluslife.domain.news.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 뉴스 등록/수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    private String title;           // 뉴스 제목
    private String content;         // 뉴스 본문
    private String summary;         // 요약 정보
    private String category;        // 카테고리
    private Boolean isNotice;       // 공지 여부
}

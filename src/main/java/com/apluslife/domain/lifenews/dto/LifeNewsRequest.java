package com.apluslife.domain.lifenews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 라이프뉴스 등록/수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifeNewsRequest {

    private String title;       // 라이프뉴스 제목
    private String content;     // 라이프뉴스 내용
}

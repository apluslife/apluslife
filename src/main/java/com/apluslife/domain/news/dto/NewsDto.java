package com.apluslife.domain.news.dto;

import com.apluslife.domain.news.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 뉴스 조회 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto {

    private Integer idx;                // 뉴스 ID
    private String title;               // 뉴스 제목
    private String content;             // 뉴스 본문
    private String summary;             // 요약 정보
    private String thumbnail;           // 썸네일 이미지 경로
    private String category;            // 카테고리
    private Integer viewCount;          // 조회수
    private Boolean isNotice;           // 공지 여부
    private LocalDateTime rDate;        // 등록일시
    private LocalDateTime uDate;        // 수정일시
    private String rDateFormatted;      // 포맷된 등록일
    private String uDateFormatted;      // 포맷된 수정일

    public static NewsDto from(News news) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return NewsDto.builder()
                .idx(news.getIdx())
                .title(news.getTitle())
                .content(news.getContent())
                .summary(news.getSummary())
                .thumbnail(news.getThumbnail())
                .category(news.getCategory())
                .viewCount(news.getViewCount())
                .isNotice(news.getIsNotice())
                .rDate(news.getRDate())
                .uDate(news.getUDate())
                .rDateFormatted(news.getRDate() != null ?
                        news.getRDate().format(formatter) : "")
                .uDateFormatted(news.getUDate() != null ?
                        news.getUDate().format(formatter) : "")
                .build();
    }
}

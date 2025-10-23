package com.apluslife.domain.lifenews.dto;

import com.apluslife.domain.lifenews.entity.LifeNews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 라이프뉴스 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifeNewsDto {

    private Integer idx;                // 라이프뉴스 ID
    private String title;               // 라이프뉴스 제목
    private String content;             // 라이프뉴스 내용
    private LocalDateTime rDate;        // 등록일
    private LocalDateTime uDate;        // 수정일
    private String registrationDate;    // 포맷된 등록일
    private String updateDate;          // 포맷된 수정일

    public static LifeNewsDto from(LifeNews lifeNews) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return LifeNewsDto.builder()
                .idx(lifeNews.getIdx())
                .title(lifeNews.getTitle())
                .content(lifeNews.getContent())
                .rDate(lifeNews.getRDate())
                .uDate(lifeNews.getUDate())
                .registrationDate(lifeNews.getRDate() != null ?
                        lifeNews.getRDate().format(formatter) : "")
                .updateDate(lifeNews.getUDate() != null ?
                        lifeNews.getUDate().format(formatter) : "")
                .build();
    }
}

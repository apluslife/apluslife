package com.apluslife.domain.announcement.dto;

import com.apluslife.domain.announcement.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 공시자료 조회 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto {

    private Integer idx;               // 공시자료 ID
    private String title;              // 공시자료 제목
    private String fileName;           // 첨부 파일명
    private String filePath;           // 첨부 파일 경로
    private LocalDateTime rDate;       // 등록일시
    private LocalDateTime uDate;       // 수정일시
    private String registrationDate;   // 포맷된 등록일
    private String updateDate;         // 포맷된 수정일

    public static AnnouncementDto from(Announcement announcement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return AnnouncementDto.builder()
                .idx(announcement.getIdx())
                .title(announcement.getTitle())
                .fileName(announcement.getFileName())
                .filePath(announcement.getFilePath())
                .rDate(announcement.getRDate())
                .uDate(announcement.getUDate())
                .registrationDate(announcement.getRDate() != null ?
                        announcement.getRDate().format(formatter) : "")
                .updateDate(announcement.getUDate() != null ?
                        announcement.getUDate().format(formatter) : "")
                .build();
    }
}

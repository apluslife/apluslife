package com.apluslife.domain.announcement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공시자료 파일 엔티티
 * aplus_파일업로드 테이블 매핑
 */
@Entity
@Table(name = "aplus_fileupload")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    @Column(name = "board_idx", nullable = false)
    private Integer boardIdx;  // 게시글 IDX

    @Column(length = 255, nullable = false)
    private String fileName;   // 파일명

    @Column(length = 500, nullable = false)
    private String filePath;   // 파일 저장 경로

    @Column(name = "categoryIdx", nullable = false)
    private Integer categoryIdx;  // 카테고리 ID

    @Column(name = "categoryName", length = 100, nullable = false)
    private String categoryName;  // 카테고리명 (공시자료, 라이프뉴스 등)
}

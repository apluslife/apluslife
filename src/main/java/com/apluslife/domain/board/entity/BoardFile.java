package com.apluslife.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_idx", nullable = false)
    private Board board;

    @Column(nullable = false, length = 255)
    private String originalFileName;  // 원본 파일명

    @Column(nullable = false, length = 255)
    private String savedFileName;  // 저장된 파일명 (UUID)

    @Column(nullable = false, length = 500)
    private String filePath;  // 파일 저장 경로

    @Column(nullable = false)
    private Long fileSize;  // 파일 크기 (bytes)

    @Column(nullable = false, length = 100)
    private String fileType;  // MIME 타입

    @Column(nullable = false, length = 50)
    private String fileCategory;  // 파일 카테고리 (image, document, archive 등)

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @Builder
    public BoardFile(String originalFileName, String savedFileName, String filePath,
                     Long fileSize, String fileType, String fileCategory) {
        this.originalFileName = originalFileName;
        this.savedFileName = savedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileCategory = fileCategory;
        this.isDeleted = false;
    }

    // 연관관계 메서드
    public void setBoard(Board board) {
        this.board = board;
    }

    // 비즈니스 메서드
    public void delete() {
        this.isDeleted = true;
    }

    public boolean isImage() {
        return "image".equals(this.fileCategory);
    }

    public String getFileExtension() {
        int lastIndex = originalFileName.lastIndexOf('.');
        if (lastIndex > 0) {
            return originalFileName.substring(lastIndex + 1).toLowerCase();
        }
        return "";
    }
}

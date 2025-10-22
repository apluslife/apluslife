package com.apluslife.domain.board.dto;

import com.apluslife.common.util.FileUtil;
import com.apluslife.domain.board.entity.BoardFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardFileDto {
    private Long idx;
    private String originalFileName;
    private String savedFileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String fileCategory;
    private LocalDateTime uploadDate;

    public static BoardFileDto from(BoardFile file) {
        return BoardFileDto.builder()
                .idx(file.getIdx())
                .originalFileName(file.getOriginalFileName())
                .savedFileName(file.getSavedFileName())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .fileType(file.getFileType())
                .fileCategory(file.getFileCategory())
                .uploadDate(file.getUploadDate())
                .build();
    }

    public String getFormattedFileSize() {
        return FileUtil.formatFileSize(fileSize);
    }

    public boolean isImage() {
        return "image".equals(fileCategory);
    }
}

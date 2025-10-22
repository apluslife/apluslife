package com.apluslife.domain.board.dto;

import com.apluslife.domain.board.entity.Board;
import com.apluslife.domain.board.entity.BoardFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class BoardDto {
    private Long idx;
    private String boardType;
    private String title;
    private String content;
    private String writer;
    private String writerId;
    private Integer viewCount;
    private Boolean isNotice;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<BoardFileDto> files;

    public static BoardDto from(Board board) {
        return BoardDto.builder()
                .idx(board.getIdx())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .writerId(board.getWriterId())
                .viewCount(board.getViewCount())
                .isNotice(board.getIsNotice())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .files(board.getFiles().stream()
                        .filter(file -> !file.getIsDeleted())
                        .map(BoardFileDto::from)
                        .collect(Collectors.toList()))
                .build();
    }

    public String getFormattedCreatedDate() {
        return createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedModifiedDate() {
        return modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}

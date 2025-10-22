package com.apluslife.domain.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 50)
    private String boardType;  // 게시판 유형 (notice, free, qna 등 확장 가능)

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(nullable = false, length = 50)
    private String writerId;  // Member.id

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Boolean isNotice = false;  // 공지사항 여부

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFile> files = new ArrayList<>();

    @Builder
    public Board(String boardType, String title, String content, String writer, String writerId, Boolean isNotice) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.writerId = writerId;
        this.isNotice = isNotice != null ? isNotice : false;
        this.viewCount = 0;
        this.isDeleted = false;
    }

    // 비즈니스 메서드
    public void update(String title, String content, Boolean isNotice) {
        this.title = title;
        this.content = content;
        this.isNotice = isNotice != null ? isNotice : false;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void addFile(BoardFile file) {
        this.files.add(file);
        file.setBoard(this);
    }

    public void removeFile(BoardFile file) {
        this.files.remove(file);
        file.setBoard(null);
    }
}

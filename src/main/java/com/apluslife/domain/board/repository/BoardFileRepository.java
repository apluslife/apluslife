package com.apluslife.domain.board.repository;

import com.apluslife.domain.board.entity.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

    // 게시글의 삭제되지 않은 파일 목록 조회
    List<BoardFile> findByBoardIdxAndIsDeletedFalseOrderByUploadDate(Long boardIdx);

    // 게시글의 이미지 파일만 조회
    List<BoardFile> findByBoardIdxAndFileCategoryAndIsDeletedFalseOrderByUploadDate(
            Long boardIdx, String fileCategory);
}

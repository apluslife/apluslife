package com.apluslife.domain.announcement.repository;

import com.apluslife.domain.announcement.entity.AnnouncementFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공시자료 파일 저장소
 */
@Repository
public interface AnnouncementFileRepository extends JpaRepository<AnnouncementFile, Integer> {

    /**
     * 게시글 IDX로 관련 파일 모두 조회
     */
    List<AnnouncementFile> findByBoardIdx(Integer boardIdx);

    /**
     * 게시글 IDX와 카테고리로 파일 조회
     */
    List<AnnouncementFile> findByBoardIdxAndCategoryIdx(Integer boardIdx, Integer categoryIdx);

    /**
     * 게시글 IDX로 파일 삭제
     */
    void deleteByBoardIdx(Integer boardIdx);
}

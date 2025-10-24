package com.apluslife.domain.announcement.repository;

import com.apluslife.domain.announcement.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 공시자료 Repository
 */
@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

    /**
     * 최신순으로 공시자료 목록 조회
     */
    @Query("SELECT a FROM Announcement a ORDER BY a.rDate DESC")
    List<Announcement> findAllOrderByLatest();

    /**
     * 제목으로 공시자료 검색
     */
    Page<Announcement> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}

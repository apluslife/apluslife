package com.apluslife.domain.news.repository;

import com.apluslife.domain.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 라이프뉴스 Repository
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    /**
     * 최신순으로 뉴스 목록 조회
     */
    @Query("SELECT n FROM News n ORDER BY n.isNotice DESC, n.rDate DESC")
    Page<News> findAllOrderByLatest(Pageable pageable);

    /**
     * 카테고리별 뉴스 조회
     */
    Page<News> findByCategory(String category, Pageable pageable);

    /**
     * 제목으로 검색
     */
    Page<News> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * 공지 뉴스 조회
     */
    @Query("SELECT n FROM News n WHERE n.isNotice = true ORDER BY n.rDate DESC")
    List<News> findAllNotices();

    /**
     * 조회수 많은 순서대로 조회
     */
    @Query("SELECT n FROM News n ORDER BY n.viewCount DESC, n.rDate DESC")
    Page<News> findPopularNews(Pageable pageable);
}

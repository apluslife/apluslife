package com.apluslife.domain.news.service;

import com.apluslife.common.util.FileUtil;
import com.apluslife.domain.news.dto.NewsDto;
import com.apluslife.domain.news.dto.NewsRequest;
import com.apluslife.domain.news.entity.News;
import com.apluslife.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 라이프뉴스 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final FileUtil fileUtil;

    /**
     * 뉴스 목록 조회 (페이징)
     */
    public Page<NewsDto> getNewsList(Pageable pageable) {
        Page<News> news = newsRepository.findAllOrderByLatest(pageable);
        return news.map(NewsDto::from);
    }

    /**
     * 뉴스 상세 조회 (조회수 증가)
     */
    @Transactional
    public NewsDto getNews(Integer idx) {
        News news = newsRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        news.increaseViewCount();
        return NewsDto.from(news);
    }

    /**
     * 뉴스 생성
     */
    @Transactional
    public Integer createNews(NewsRequest request, MultipartFile thumbnailFile) throws IOException {
        String thumbnail = null;

        // 썸네일 업로드 처리
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            if (!fileUtil.isImageFile(thumbnailFile.getOriginalFilename())) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }
            Map<String, Object> fileInfo = fileUtil.saveFile(thumbnailFile);
            thumbnail = (String) fileInfo.get("filePath");
        }

        // 뉴스 생성
        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(request.getSummary())
                .thumbnail(thumbnail)
                .category(request.getCategory() != null ? request.getCategory() : "일반뉴스")
                .isNotice(request.getIsNotice() != null ? request.getIsNotice() : false)
                .rDate(LocalDateTime.now())
                .uDate(LocalDateTime.now())
                .build();

        News saved = newsRepository.save(news);
        return saved.getIdx();
    }

    /**
     * 뉴스 수정
     */
    @Transactional
    public void updateNews(Integer idx, NewsRequest request, MultipartFile thumbnailFile) throws IOException {
        News news = newsRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        // 기본 정보 수정
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setSummary(request.getSummary());
        news.setCategory(request.getCategory() != null ? request.getCategory() : "일반뉴스");
        news.setIsNotice(request.getIsNotice() != null ? request.getIsNotice() : false);
        news.setUDate(LocalDateTime.now());

        // 썸네일 업로드 처리
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            if (!fileUtil.isImageFile(thumbnailFile.getOriginalFilename())) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }

            // 기존 썸네일 삭제
            if (news.getThumbnail() != null && !news.getThumbnail().isEmpty()) {
                fileUtil.deleteFile(news.getThumbnail());
            }

            Map<String, Object> fileInfo = fileUtil.saveFile(thumbnailFile);
            news.setThumbnail((String) fileInfo.get("filePath"));
        }

        newsRepository.save(news);
    }

    /**
     * 뉴스 삭제
     */
    @Transactional
    public void deleteNews(Integer idx) {
        News news = newsRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        // 썸네일 삭제
        if (news.getThumbnail() != null && !news.getThumbnail().isEmpty()) {
            fileUtil.deleteFile(news.getThumbnail());
        }

        newsRepository.deleteById(idx);
    }

    /**
     * 뉴스 검색
     */
    public Page<NewsDto> searchNews(String keyword, Pageable pageable) {
        Page<News> news = newsRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return news.map(NewsDto::from);
    }

    /**
     * 카테고리별 뉴스 조회
     */
    public Page<NewsDto> getNewsByCategory(String category, Pageable pageable) {
        Page<News> news = newsRepository.findByCategory(category, pageable);
        return news.map(NewsDto::from);
    }

    /**
     * 공지 뉴스 조회
     */
    public List<NewsDto> getNoticeNews() {
        List<News> notices = newsRepository.findAllNotices();
        return notices.stream()
                .map(NewsDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 인기 뉴스 조회
     */
    public Page<NewsDto> getPopularNews(Pageable pageable) {
        Page<News> news = newsRepository.findPopularNews(pageable);
        return news.map(NewsDto::from);
    }

    /**
     * 공지 여부 토글
     */
    @Transactional
    public void toggleNotice(Integer idx) {
        News news = newsRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다."));

        news.toggleNotice();
        newsRepository.save(news);
    }
}

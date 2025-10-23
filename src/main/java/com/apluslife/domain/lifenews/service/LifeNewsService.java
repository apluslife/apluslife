package com.apluslife.domain.lifenews.service;

import com.apluslife.domain.lifenews.dto.LifeNewsDto;
import com.apluslife.domain.lifenews.dto.LifeNewsRequest;
import com.apluslife.domain.lifenews.entity.LifeNews;
import com.apluslife.domain.lifenews.mapper.LifeNewsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 라이프뉴스 관리 서비스
 * MyBatis를 사용한 데이터베이스 조작
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LifeNewsService {

    private final LifeNewsMapper lifeNewsMapper;

    /**
     * 라이프뉴스 목록 조회 (최신순)
     */
    public List<LifeNewsDto> getLifeNewsList() {
        List<LifeNews> lifeNewsList = lifeNewsMapper.selectAllOrderByLatest();
        return lifeNewsList.stream()
                .map(LifeNewsDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 라이프뉴스 상세 조회
     */
    public LifeNewsDto getLifeNews(Integer idx) {
        LifeNews lifeNews = lifeNewsMapper.selectByIdx(idx);
        if (lifeNews == null) {
            throw new IllegalArgumentException("라이프뉴스를 찾을 수 없습니다.");
        }
        return LifeNewsDto.from(lifeNews);
    }

    /**
     * 라이프뉴스 검색 (제목)
     */
    public List<LifeNewsDto> searchLifeNews(String searchText) {
        List<LifeNews> lifeNewsList = lifeNewsMapper.selectByTitleLike(searchText);
        return lifeNewsList.stream()
                .map(LifeNewsDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 라이프뉴스 생성
     */
    @Transactional
    public Integer createLifeNews(LifeNewsRequest request) {
        LifeNews lifeNews = LifeNews.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .rDate(LocalDateTime.now())
                .uDate(LocalDateTime.now())
                .build();

        int result = lifeNewsMapper.insertLifeNews(lifeNews);
        if (result > 0) {
            log.info("라이프뉴스 생성 완료: idx={}", lifeNews.getIdx());
            return lifeNews.getIdx();
        } else {
            throw new RuntimeException("라이프뉴스 생성에 실패했습니다.");
        }
    }

    /**
     * 라이프뉴스 수정
     */
    @Transactional
    public void updateLifeNews(Integer idx, LifeNewsRequest request) {
        LifeNews existingNews = lifeNewsMapper.selectByIdx(idx);
        if (existingNews == null) {
            throw new IllegalArgumentException("라이프뉴스를 찾을 수 없습니다.");
        }

        LifeNews lifeNews = LifeNews.builder()
                .idx(idx)
                .title(request.getTitle())
                .content(request.getContent())
                .rDate(existingNews.getRDate())
                .uDate(LocalDateTime.now())
                .build();

        int result = lifeNewsMapper.updateLifeNews(lifeNews);
        if (result > 0) {
            log.info("라이프뉴스 수정 완료: idx={}", idx);
        } else {
            throw new RuntimeException("라이프뉴스 수정에 실패했습니다.");
        }
    }

    /**
     * 라이프뉴스 삭제 (단일)
     */
    @Transactional
    public void deleteLifeNews(Integer idx) {
        LifeNews lifeNews = lifeNewsMapper.selectByIdx(idx);
        if (lifeNews == null) {
            throw new IllegalArgumentException("라이프뉴스를 찾을 수 없습니다.");
        }

        int result = lifeNewsMapper.deleteByIdx(idx);
        if (result > 0) {
            log.info("라이프뉴스 삭제 완료: idx={}", idx);
        } else {
            throw new RuntimeException("라이프뉴스 삭제에 실패했습니다.");
        }
    }

    /**
     * 라이프뉴스 삭제 (다중)
     */
    @Transactional
    public void deleteLifeNewsList(List<Integer> idxList) {
        if (idxList == null || idxList.isEmpty()) {
            throw new IllegalArgumentException("삭제할 라이프뉴스를 선택해주세요.");
        }

        int result = lifeNewsMapper.deleteByIdxList(idxList);
        if (result > 0) {
            log.info("라이프뉴스 다중 삭제 완료: count={}", result);
        } else {
            throw new RuntimeException("라이프뉴스 삭제에 실패했습니다.");
        }
    }
}

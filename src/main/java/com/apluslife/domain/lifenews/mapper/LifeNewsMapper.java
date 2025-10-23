package com.apluslife.domain.lifenews.mapper;

import com.apluslife.domain.lifenews.entity.LifeNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 라이프뉴스 MyBatis Mapper
 * XML 파일: LifeNewsMapper.xml
 */
@Mapper
public interface LifeNewsMapper {

    /**
     * 라이프뉴스 목록 조회 (최신순)
     */
    List<LifeNews> selectAllOrderByLatest();

    /**
     * 라이프뉴스 상세 조회
     */
    LifeNews selectByIdx(@Param("idx") Integer idx);

    /**
     * 라이프뉴스 제목으로 검색
     */
    List<LifeNews> selectByTitleLike(@Param("searchText") String searchText);

    /**
     * 라이프뉴스 등록
     */
    int insertLifeNews(@Param("lifeNews") LifeNews lifeNews);

    /**
     * 라이프뉴스 수정
     */
    int updateLifeNews(@Param("lifeNews") LifeNews lifeNews);

    /**
     * 라이프뉴스 삭제 (단일)
     */
    int deleteByIdx(@Param("idx") Integer idx);

    /**
     * 라이프뉴스 삭제 (다중)
     */
    int deleteByIdxList(@Param("idxList") List<Integer> idxList);
}

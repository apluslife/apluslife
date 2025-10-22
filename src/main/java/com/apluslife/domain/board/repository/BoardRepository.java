package com.apluslife.domain.board.repository;

import com.apluslife.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 삭제되지 않은 게시글 조회 (게시판 유형별)
    Page<Board> findByBoardTypeAndIsDeletedFalseOrderByIsNoticeDescCreatedDateDesc(
            String boardType, Pageable pageable);

    // 삭제되지 않은 게시글 조회 (전체)
    Page<Board> findByIsDeletedFalseOrderByIsNoticeDescCreatedDateDesc(Pageable pageable);

    // 제목 검색
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.isDeleted = false " +
           "AND b.title LIKE %:keyword% ORDER BY b.isNotice DESC, b.createdDate DESC")
    Page<Board> searchByTitle(@Param("boardType") String boardType,
                               @Param("keyword") String keyword,
                               Pageable pageable);

    // 내용 검색
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.isDeleted = false " +
           "AND b.content LIKE %:keyword% ORDER BY b.isNotice DESC, b.createdDate DESC")
    Page<Board> searchByContent(@Param("boardType") String boardType,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);

    // 제목 + 내용 검색
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.isDeleted = false " +
           "AND (b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
           "ORDER BY b.isNotice DESC, b.createdDate DESC")
    Page<Board> searchByTitleOrContent(@Param("boardType") String boardType,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    // 작성자 검색
    @Query("SELECT b FROM Board b WHERE b.boardType = :boardType AND b.isDeleted = false " +
           "AND b.writer LIKE %:keyword% ORDER BY b.isNotice DESC, b.createdDate DESC")
    Page<Board> searchByWriter(@Param("boardType") String boardType,
                                @Param("keyword") String keyword,
                                Pageable pageable);

    // 삭제되지 않은 게시글 단건 조회
    Optional<Board> findByIdxAndIsDeletedFalse(Long idx);
}

package com.apluslife.domain.log.repository;

import com.apluslife.domain.log.entity.UserActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

    // 사용자별 로그 조회
    Page<UserActionLog> findByUserIdOrderByActionTimeDesc(String userId, Pageable pageable);

    // 액션 유형별 로그 조회
    Page<UserActionLog> findByActionTypeOrderByActionTimeDesc(String actionType, Pageable pageable);

    // 기간별 로그 조회
    @Query("SELECT l FROM UserActionLog l WHERE l.actionTime BETWEEN :startDate AND :endDate ORDER BY l.actionTime DESC")
    Page<UserActionLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    // 사용자 + 기간별 로그 조회
    @Query("SELECT l FROM UserActionLog l WHERE l.userId = :userId AND l.actionTime BETWEEN :startDate AND :endDate ORDER BY l.actionTime DESC")
    List<UserActionLog> findByUserIdAndDateRange(@Param("userId") String userId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // 실패한 액션 조회
    Page<UserActionLog> findByActionResultOrderByActionTimeDesc(String actionResult, Pageable pageable);

    // 특정 날짜 이전 로그 삭제용
    void deleteByActionTimeBefore(LocalDateTime date);
}

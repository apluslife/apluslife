package com.apluslife.domain.log.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_action_log", indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_action_time", columnList = "actionTime"),
        @Index(name = "idx_action_type", columnList = "actionType")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 50)
    private String userId;  // 로그인 ID

    @Column(nullable = false, length = 100)
    private String userName;  // 사용자 이름

    @Column(nullable = false, length = 50)
    private String actionType;  // 액션 유형 (LOGIN, LOGOUT, VIEW, CREATE, UPDATE, DELETE 등)

    @Column(nullable = false, length = 200)
    private String actionTarget;  // 액션 대상 (예: 게시글 번호, 페이지 경로 등)

    @Column(length = 1000)
    private String actionDetail;  // 액션 상세 정보

    @Column(nullable = false, length = 50)
    private String ipAddress;  // 접속 IP

    @Column(length = 500)
    private String userAgent;  // 브라우저 정보

    @Column(nullable = false, length = 20)
    private String actionResult;  // 액션 결과 (SUCCESS, FAIL, ERROR)

    @Column(length = 1000)
    private String errorMessage;  // 에러 메시지 (실패 시)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime actionTime;

    @Builder
    public UserActionLog(String userId, String userName, String actionType, String actionTarget,
                        String actionDetail, String ipAddress, String userAgent,
                        String actionResult, String errorMessage) {
        this.userId = userId;
        this.userName = userName;
        this.actionType = actionType;
        this.actionTarget = actionTarget;
        this.actionDetail = actionDetail;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.actionResult = actionResult;
        this.errorMessage = errorMessage;
    }
}

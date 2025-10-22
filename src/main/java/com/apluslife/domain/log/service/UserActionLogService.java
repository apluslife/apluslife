package com.apluslife.domain.log.service;

import com.apluslife.domain.log.entity.UserActionLog;
import com.apluslife.domain.log.repository.UserActionLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionLogService {

    private final UserActionLogRepository userActionLogRepository;

    /**
     * 사용자 액션 로그 저장 (비동기)
     */
    @Async
    @Transactional
    public void saveActionLog(String actionType, String actionTarget, String actionDetail, String actionResult) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return; // 로그인하지 않은 사용자는 로그 저장 안 함
            }

            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return;
            }

            String userId = auth.getName();
            String userName = getUserName(auth);
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            UserActionLog actionLog = UserActionLog.builder()
                    .userId(userId)
                    .userName(userName)
                    .actionType(actionType)
                    .actionTarget(actionTarget)
                    .actionDetail(actionDetail)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .actionResult(actionResult)
                    .build();

            userActionLogRepository.save(actionLog);

            log.info("[액션로그] 사용자: {}, 액션: {}, 대상: {}, 결과: {}", userId, actionType, actionTarget, actionResult);

        } catch (Exception e) {
            log.error("액션 로그 저장 실패", e);
        }
    }

    /**
     * 에러 포함 액션 로그 저장
     */
    @Async
    @Transactional
    public void saveActionLogWithError(String actionType, String actionTarget, String actionDetail,
                                       String actionResult, String errorMessage) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return;
            }

            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return;
            }

            String userId = auth.getName();
            String userName = getUserName(auth);
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            UserActionLog actionLog = UserActionLog.builder()
                    .userId(userId)
                    .userName(userName)
                    .actionType(actionType)
                    .actionTarget(actionTarget)
                    .actionDetail(actionDetail)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .actionResult(actionResult)
                    .errorMessage(errorMessage)
                    .build();

            userActionLogRepository.save(actionLog);

            log.warn("[액션로그-실패] 사용자: {}, 액션: {}, 결과: {}, 에러: {}", userId, actionType, actionResult, errorMessage);

        } catch (Exception e) {
            log.error("에러 액션 로그 저장 실패", e);
        }
    }

    /**
     * 사용자별 로그 조회
     */
    public Page<UserActionLog> getUserLogs(String userId, Pageable pageable) {
        return userActionLogRepository.findByUserIdOrderByActionTimeDesc(userId, pageable);
    }

    /**
     * 기간별 로그 조회
     */
    public Page<UserActionLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return userActionLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * 액션 유형별 로그 조회
     */
    public Page<UserActionLog> getLogsByActionType(String actionType, Pageable pageable) {
        return userActionLogRepository.findByActionTypeOrderByActionTimeDesc(actionType, pageable);
    }

    /**
     * 실패한 액션 조회
     */
    public Page<UserActionLog> getFailedLogs(Pageable pageable) {
        return userActionLogRepository.findByActionResultOrderByActionTimeDesc("FAIL", pageable);
    }

    /**
     * 오래된 로그 삭제 (90일 이전)
     */
    @Transactional
    public void deleteOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        userActionLogRepository.deleteByActionTimeBefore(cutoffDate);
        log.info("{}일 이전 로그 삭제 완료", daysToKeep);
    }

    /**
     * 현재 HttpServletRequest 가져오기
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 사용자 이름 추출
     */
    private String getUserName(Authentication auth) {
        try {
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                return ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            }
            return auth.getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 클라이언트 IP 주소 가져오기 (프록시 고려)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

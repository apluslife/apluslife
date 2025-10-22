package com.apluslife.common.aspect;

import com.apluslife.domain.log.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserActionLogAspect {

    private final UserActionLogService actionLogService;

    /**
     * 모든 Controller 메서드를 대상으로 함
     */
    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    /**
     * 성공적인 실행 후 로그 기록
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            // 로그인, 로그아웃은 별도로 처리하므로 제외
            if ("login".equals(methodName) || "logout".equals(methodName)) {
                return;
            }

            Method method = getMethod(joinPoint);
            if (method == null) {
                return;
            }

            String actionType = determineActionType(method, methodName);
            String actionTarget = className + "." + methodName;
            String actionDetail = buildActionDetail(joinPoint);

            actionLogService.saveActionLog(actionType, actionTarget, actionDetail, "SUCCESS");

        } catch (Exception e) {
            log.error("로그 기록 중 오류 발생", e);
        }
    }

    /**
     * 예외 발생 시 로그 기록
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            Method method = getMethod(joinPoint);
            if (method == null) {
                return;
            }

            String actionType = determineActionType(method, methodName);
            String actionTarget = className + "." + methodName;
            String actionDetail = buildActionDetail(joinPoint);
            String errorMessage = exception.getMessage();

            actionLogService.saveActionLogWithError(actionType, actionTarget, actionDetail, "ERROR", errorMessage);

        } catch (Exception e) {
            log.error("에러 로그 기록 중 오류 발생", e);
        }
    }

    /**
     * 메서드 가져오기
     */
    private Method getMethod(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            return joinPoint.getTarget().getClass().getMethod(methodName, paramTypes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * HTTP Method에 따라 액션 유형 결정
     */
    private String determineActionType(Method method, String methodName) {
        if (method.isAnnotationPresent(GetMapping.class) || method.isAnnotationPresent(RequestMapping.class)) {
            if (methodName.contains("list") || methodName.contains("List")) {
                return "VIEW_LIST";
            } else if (methodName.contains("detail") || methodName.contains("Detail")) {
                return "VIEW_DETAIL";
            }
            return "VIEW";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            if (methodName.contains("delete") || methodName.contains("Delete")) {
                return "DELETE";
            } else if (methodName.contains("update") || methodName.contains("Update") || methodName.contains("edit") || methodName.contains("Edit")) {
                return "UPDATE";
            }
            return "CREATE";
        } else if (method.isAnnotationPresent(PutMapping.class) || method.isAnnotationPresent(PatchMapping.class)) {
            return "UPDATE";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        }

        return "ACTION";
    }

    /**
     * 액션 상세 정보 구성
     */
    private String buildActionDetail(JoinPoint joinPoint) {
        try {
            StringBuilder detail = new StringBuilder();
            Object[] args = joinPoint.getArgs();

            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) continue;

                String argType = args[i].getClass().getSimpleName();

                // 민감한 정보 제외
                if (argType.contains("Password") || argType.contains("HttpServlet") ||
                    argType.contains("Model") || argType.contains("BindingResult")) {
                    continue;
                }

                if (detail.length() > 0) {
                    detail.append(", ");
                }

                detail.append("arg").append(i).append("=").append(args[i].toString());

                // 상세 정보가 너무 길면 잘라냄
                if (detail.length() > 500) {
                    detail.append("...");
                    break;
                }
            }

            return detail.toString();
        } catch (Exception e) {
            return "N/A";
        }
    }
}

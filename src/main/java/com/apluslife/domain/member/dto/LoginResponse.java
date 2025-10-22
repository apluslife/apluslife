package com.apluslife.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String status;      // 처리 결과 (성공: SUCCESS, 실패: 에러코드)
    private String message;     // 실패 시 메시지
    private Map<String, Object> returnList;  // 전송된 parameter 값

    public static LoginResponse success(int result, int dateOfDiff, String memGubun,
                                       String memName, String custNo, String id) {
        Map<String, Object> data = new HashMap<>();
        data.put("res1", result);
        data.put("res2", dateOfDiff);
        data.put("res3", memGubun);
        data.put("res4", memName);
        data.put("res5", custNo);
        data.put("res6", id);

        return LoginResponse.builder()
                .status("SUCCESS")
                .message(null)
                .returnList(data)
                .build();
    }

    public static LoginResponse fail(String errorCode, String message) {
        return LoginResponse.builder()
                .status(errorCode)
                .message(message)
                .returnList(new HashMap<>())
                .build();
    }
}

package com.apluslife.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String gubun;  // API구분 (로그인: 2)
    private String id;     // 아이디
    private String pw;     // 비밀번호
}

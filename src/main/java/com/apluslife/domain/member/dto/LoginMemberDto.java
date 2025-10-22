package com.apluslife.domain.member.dto;

/**
 * 로그인 쿼리 결과를 담는 인터페이스 Projection
 * Spring Data JPA가 자동으로 구현체를 생성합니다
 */
public interface LoginMemberDto {

    String getId();              // 아이디
    String getPw();              // 비밀번호
    String getWithdraw();        // 탈퇴여부 (2: 탈퇴회원, N: 정상회원)
    Integer getDateOFdiff();     // 비밀번호 변경 경과일자
    String getMemberGubun();     // 회원구분 (mem: 라이프회원, nor: 일반회원)
    String getMemberName();      // 이름
    String getCustNo();          // 라이프 회원번호
}

package com.apluslife.domain.admin.dto;

/**
 * 관리자 로그인 쿼리 결과를 담는 인터페이스 Projection
 * Spring Data JPA가 자동으로 구현체를 생성합니다
 */
public interface LoginAdminDto {

    Integer getIdx();           // 관리자 idx
    String getAdminId();        // 관리자 아이디
    String getAdminPw();        // 관리자 비밀번호
    String getManageLevel();    // 관리자 레벨
    String getAdminName();      // 관리자 이름
}

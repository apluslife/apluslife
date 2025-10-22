package com.apluslife.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;  // Legacy DB의 primary key

    @Column(name = "id", length = 30)
    private String id;

    @Column(name = "pw", length = 30)
    private String pw;

    @Column(name = "enpw")
    private byte[] enpw;  // Legacy DB에서는 varbinary(128)

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "jumin", length = 20)
    private String jumin;  // 주민번호

    @Column(name = "brithday", length = 50)
    private String brithday;  // 생년월일

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "postcd", length = 50)
    private String postcd;  // 우편번호

    @Column(name = "addr1", length = 200)
    private String addr1;  // 주소1

    @Column(name = "addr2", length = 100)
    private String addr2;  // 주소2

    @Column(name = "tel", length = 50)
    private String tel;  // 전화번호

    @Column(name = "hp", length = 50)
    private String hp;  // 휴대폰

    @Column(name = "email_susin", length = 5)
    private String emailSusin;  // 이메일 수신 동의

    @Column(name = "post_susin", length = 5)
    private String postSusin;  // 우편 수신 동의

    @Column(name = "sms_susin", length = 5)
    private String smsSusin;  // SMS 수신 동의

    @Column(name = "member_del", length = 5)
    private String memberDel;  // 회원 삭제 여부

    @Column(name = "r_date")
    private LocalDateTime rDate;  // 등록일

    @Column(name = "u_date")
    private LocalDateTime uDate;  // 수정일

    @Column(name = "withdraw", length = 5)
    private String withdraw;  // 탈퇴 여부

    @Column(name = "w_date")
    private LocalDateTime wDate;  // 탈퇴일

    @Column(name = "w_cause", length = 500)
    private String wCause;  // 탈퇴 사유

    @Column(name = "pwdate", nullable = false)
    private LocalDateTime pwdate;  // 비밀번호 변경일

    @Column(name = "member_gubun", length = 5)
    private String memberGubun;  // mem: 라이프회원, nor: 일반회원

    @Column(name = "visit_num", length = 5)
    private String visitNum;  // 방문 횟수

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;  // 메모

    @Column(name = "hcare", length = 2)
    private String hcare;

    @Column(name = "connectno", length = 30)
    private String connectno;

    @Column(name = "recivedate", length = 10)
    private String recivedate;

    @Column(name = "cmpname", length = 50)
    private String cmpname;

    @Column(name = "cmptel", length = 50)
    private String cmptel;

    @Column(name = "cust_no", length = 15)
    private String custNo;  // 라이프 회원번호

    @Column(name = "ci", length = 100)
    private String ci;

    @Column(name = "di", length = 100)
    private String di;

    // 관리자 권한 판단을 위한 메서드 (legacy DB에는 manage_level이 없으므로 id로 판단)
    public boolean isAdmin() {
        return "admin".equals(this.id) || "administrator".equals(this.id);
    }

    public boolean isUser() {
        return !isAdmin();
    }

    public boolean isWithdrawn() {
        return "2".equals(this.withdraw);
    }

    public boolean isLifeMember() {
        return "mem".equals(this.memberGubun);
    }

    public boolean isNormalMember() {
        return "nor".equals(this.memberGubun);
    }
}

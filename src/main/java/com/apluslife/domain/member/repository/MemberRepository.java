package com.apluslife.domain.member.repository;

import com.apluslife.domain.member.dto.LoginMemberDto;
import com.apluslife.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findById(String id);

    boolean existsById(String id);

    boolean existsByEmail(String email);

    /**
     * 로그인 쿼리
     * PWDCOMPARE 함수를 사용하여 SQL Server의 암호화된 비밀번호 비교
     */
    @Query(value = """
        SELECT
            id,
            pw,
            withdraw,
            DATEDIFF(d, pwdate, GETDATE()) as dateOFdiff,
            member_gubun as memberGubun,
            name as memberName,
            cust_no as custNo
        FROM member
        WHERE id = :id
        AND PWDCOMPARE(:pw, enpw) = 1
        """, nativeQuery = true)
    Optional<LoginMemberDto> findByIdAndPassword(@Param("id") String id, @Param("pw") String pw);
}

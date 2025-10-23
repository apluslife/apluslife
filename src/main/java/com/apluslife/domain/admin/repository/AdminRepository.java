package com.apluslife.domain.admin.repository;

import com.apluslife.domain.admin.dto.LoginAdminDto;
import com.apluslife.domain.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);

    /**
     * 관리자 로그인 쿼리
     * admin_pw 컬럼에 평문 비밀번호가 저장되어 있음
     * 평문으로 직접 비교
     */
    @Query(value = """
        SELECT
            idx,
            admin_id as adminId,
            admin_pw as adminPw,
            manage_level as manageLevel,
            admin_name as adminName
        FROM admin
        WHERE admin_id = :adminId
        AND admin_pw = :adminPw
        """, nativeQuery = true)
    Optional<LoginAdminDto> findByAdminIdAndPassword(@Param("adminId") String adminId, @Param("adminPw") String adminPw);
}

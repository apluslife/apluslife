package com.apluslife.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "admin_id", length = 50)
    private String adminId;

    @Column(name = "admin_pw", length = 100)
    private String adminPw;

    @Column(name = "manage_level", length = 10)
    private String manageLevel;  // 관리자 레벨

    @Column(name = "admin_name", length = 50)
    private String adminName;

    /**
     * 관리자 레벨 확인
     */
    public boolean isSuperAdmin() {
        return "1".equals(this.manageLevel);
    }

    public boolean isGeneralAdmin() {
        return "2".equals(this.manageLevel);
    }
}

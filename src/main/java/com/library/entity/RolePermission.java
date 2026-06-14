package com.library.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Một ô trong ma trận phân quyền (vai trò × quyền).
 * Admin bật/tắt trực tiếp trên UI, không cần sửa code.
 */
@Entity
@Table(name = "role_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_role", "permission"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 30)
    private Permission permission;

    @Column(name = "granted", nullable = false)
    private Boolean granted = false;
}

package com.library.repository;

import com.library.entity.Permission;
import com.library.entity.RolePermission;
import com.library.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Optional<RolePermission> findByUserRoleAndPermission(UserRole userRole, Permission permission);
    List<RolePermission> findByUserRole(UserRole userRole);
}

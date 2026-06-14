package com.library.service;

import com.library.entity.Permission;
import com.library.entity.RolePermission;
import com.library.entity.UserRole;
import com.library.repository.RolePermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

/**
 * Đọc/ghi ma trận phân quyền. Dùng để kiểm tra quyền ở controller và ẩn/hiện nút ở template.
 */
@Service
@Transactional
public class PermissionService {

    private final RolePermissionRepository repo;

    public PermissionService(RolePermissionRepository repo) {
        this.repo = repo;
    }

    /** Vai trò này có quyền đó không (chưa có bản ghi = chưa cấp). */
    @Transactional(readOnly = true)
    public boolean has(UserRole role, Permission permission) {
        if (role == null) return false;
        return repo.findByUserRoleAndPermission(role, permission)
                .map(rp -> Boolean.TRUE.equals(rp.getGranted()))
                .orElse(false);
    }

    /** Tập các quyền đang được cấp cho vai trò (dùng cho hiển thị). */
    @Transactional(readOnly = true)
    public Set<Permission> grantedFor(UserRole role) {
        Set<Permission> result = EnumSet.noneOf(Permission.class);
        for (RolePermission rp : repo.findByUserRole(role)) {
            if (Boolean.TRUE.equals(rp.getGranted())) {
                result.add(rp.getPermission());
            }
        }
        return result;
    }

    /**
     * Lưu lại toàn bộ quyền của MỘT vai trò (theo giao diện "chọn vai trò → bật/tắt quyền").
     * Quyền nằm trong {@code granted} sẽ được cấp; mọi quyền còn lại của vai trò bị thu.
     */
    public void updateGrantsForRole(UserRole role, Set<Permission> granted) {
        for (Permission permission : Permission.values()) {
            RolePermission rp = repo.findByUserRoleAndPermission(role, permission)
                    .orElseGet(() -> {
                        RolePermission n = new RolePermission();
                        n.setUserRole(role);
                        n.setPermission(permission);
                        return n;
                    });
            rp.setGranted(granted.contains(permission));
            repo.save(rp);
        }
    }
}

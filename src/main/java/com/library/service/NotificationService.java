package com.library.service;

import com.library.entity.Notification;
import com.library.entity.NotificationType;
import com.library.entity.User;
import com.library.entity.UserRole;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tạo và đọc thông báo in-app. Các sự kiện nghiệp vụ (mượn/trả/huỷ/...) gọi vào đây.
 */
@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /** Gửi cho một người dùng. */
    public void notify(User user, NotificationType type, String message, String link) {
        if (user == null) return;
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setMessage(message);
        n.setLink(link);
        n.setRead(false);
        notificationRepository.save(n);
    }

    /** Gửi cho tất cả người dùng thuộc một vai trò (vd báo mọi thủ thư có đơn mới). */
    public void notifyRole(UserRole role, NotificationType type, String message, String link) {
        for (User u : userRepository.findByRole(role)) {
            notify(u, type, message, link);
        }
    }

    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> recent(User user) {
        return notificationRepository.findTop5ByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> findAll(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public void markRead(Long id, User user) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getUser().getId().equals(user.getId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    public void markAllRead(User user) {
        List<Notification> list = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (Notification n : list) {
            if (!Boolean.TRUE.equals(n.getRead())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }
}

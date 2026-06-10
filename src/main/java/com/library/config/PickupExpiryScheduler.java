package com.library.config;

import com.library.service.LoanService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Định kỳ huỷ các đơn chờ lấy đã quá hạn 24h (SV không đến lấy sách).
 */
@Component
public class PickupExpiryScheduler {

    private final LoanService loanService;

    public PickupExpiryScheduler(LoanService loanService) {
        this.loanService = loanService;
    }

    // Chạy mỗi 10 phút
    @Scheduled(fixedRate = 600_000)
    public void cancelExpiredPickups() {
        loanService.cancelExpiredPickups();
    }
}

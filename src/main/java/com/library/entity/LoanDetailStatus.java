package com.library.entity;

public enum LoanDetailStatus {
    RESERVED,  // Đã giữ chỗ, chờ SV đến lấy (chưa có hạn trả)
    BORROWING, // Đang mượn (đã lấy sách, đã có hạn trả)
    RETURNED,
    OVERDUE
}

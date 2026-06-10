package com.library.entity;

public enum LoanStatus {
    AWAITING_PICKUP, // Đã tạo đơn, có mã mượn, đang chờ SV đến lấy (giữ chỗ sách)
    BORROWED,        // Thủ thư đã giao sách, đang mượn
    RETURNED,        // Đã trả hết
    CANCELLED        // Thủ thư huỷ hoặc tự huỷ do quá 24h chưa lấy
}

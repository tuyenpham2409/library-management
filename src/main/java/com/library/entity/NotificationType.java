package com.library.entity;

/**
 * Mức độ thông báo — quyết định màu badge và icon hiển thị. Nội dung cụ thể nằm ở message.
 */
public enum NotificationType {
    SUCCESS("success", "bi-check-circle-fill"),
    INFO("info", "bi-info-circle-fill"),
    WARNING("warning", "bi-exclamation-triangle-fill"),
    DANGER("danger", "bi-x-circle-fill");

    private final String cssClass;
    private final String icon;

    NotificationType(String cssClass, String icon) {
        this.cssClass = cssClass;
        this.icon = icon;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getIcon() {
        return icon;
    }
}

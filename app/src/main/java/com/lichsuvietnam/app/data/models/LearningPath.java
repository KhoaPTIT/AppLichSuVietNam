package com.lichsuvietnam.app.data.models;

/**
 * Model giao diện cho một lộ trình học.
 * Lớp này tổng hợp dữ liệu từ LearningPathEntity và tiến độ người dùng
 * để adapter hiển thị phần trăm hoàn thành.
 */
public class LearningPath {
    // ID lộ trình, dùng để mở LearningPathDetailActivity.
    private int id;
    // Tên lộ trình học.
    private String title;
    // Tổng số bài học trong lộ trình.
    private int totalLessons;
    // Số bài người dùng đã hoàn thành.
    private int completedLessons;
    // Icon/emoji đại diện cho lộ trình.
    private String icon;

    /** Khởi tạo model lộ trình học để hiển thị trên UI. */
    public LearningPath(int id, String title, int totalLessons, int completedLessons, String icon) {
        this.id = id;
        this.title = title;
        this.totalLessons = totalLessons;
        this.completedLessons = completedLessons;
        this.icon = icon;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getTotalLessons() { return totalLessons; }
    public int getCompletedLessons() { return completedLessons; }
    public String getIcon() { return icon; }

    /** Tính phần trăm hoàn thành của lộ trình. */
    public int getProgressPercent() { return Math.round((float) completedLessons / totalLessons * 100); }

    /** Kiểm tra lộ trình đã hoàn thành toàn bộ bài học hay chưa. */
    public boolean isComplete() { return completedLessons >= totalLessons; }
}

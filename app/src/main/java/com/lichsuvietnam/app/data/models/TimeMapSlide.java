package com.lichsuvietnam.app.data.models;

/**
 * Model giao diện cho một giai đoạn trong chức năng Bản đồ thời gian.
 * Lớp này lưu nội dung hiển thị, thứ tự sắp xếp và nguồn ảnh bản đồ
 * dạng drawable nội bộ hoặc URL từ xa.
 */
public class TimeMapSlide {
    // ID ổn định của slide.
    private int id;
    // Tiêu đề chính hiển thị dưới bản đồ.
    private String title;
    // Nhãn giai đoạn hoặc triều đại.
    private String period;
    // Khoảng năm dạng chữ để hiển thị cho người dùng.
    private String yearLabel;
    // Thứ tự sắp xếp để giữ đúng mạch thời gian.
    private int sortOrder;
    // URL ảnh từ xa, chỉ dùng khi không có drawable nội bộ.
    private String imageUrl;
    // Nội dung giải thích lịch sử hiển thị trong thẻ mô tả.
    private String description;
    // Resource drawable nội bộ; giá trị 0 nghĩa là không có ảnh nội bộ.
    private int localImageResId; // 0 nghĩa là không có ảnh nội bộ

    /**
     * Khởi tạo slide có hỗ trợ ảnh bản đồ đóng gói trong drawable.
     */
    public TimeMapSlide(int id, String title, String period, String yearLabel,
                        int sortOrder, String imageUrl, String description,
                        int localImageResId) {
        this.id = id;
        this.title = title;
        this.period = period;
        this.yearLabel = yearLabel;
        this.sortOrder = sortOrder;
        this.imageUrl = imageUrl;
        this.description = description;
        this.localImageResId = localImageResId;
    }

    /**
     * Khởi tạo slide không có ảnh drawable nội bộ.
     * Constructor này giữ tương thích với code cũ bằng cách gán localImageResId = 0.
     */
    public TimeMapSlide(int id, String title, String period, String yearLabel,
                        int sortOrder, String imageUrl, String description) {
        this(id, title, period, yearLabel, sortOrder, imageUrl, description, 0);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getYearLabel() { return yearLabel; }
    public void setYearLabel(String yearLabel) { this.yearLabel = yearLabel; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getLocalImageResId() { return localImageResId; }
    public void setLocalImageResId(int localImageResId) { this.localImageResId = localImageResId; }

    /**
     * Kiểm tra slide có ảnh drawable nội bộ hay không.
     * Nếu true thì màn hình ưu tiên tải ảnh bằng localImageResId thay vì imageUrl.
     */
    public boolean hasLocalImage() { return localImageResId != 0; }
}

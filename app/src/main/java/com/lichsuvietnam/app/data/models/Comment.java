package com.lichsuvietnam.app.data.models;

/**
 * Model giao diện cho một bình luận trong chức năng cộng đồng.
 * Lớp này tách dữ liệu hiển thị khỏi CommentEntity của Room để adapter xử lý
 * trạng thái reply và trạng thái đã thích dễ hơn.
 */
public class Comment {
    // ID bình luận tương ứng trong database.
    private long id;
    // Tên người bình luận.
    private String name;
    // Nội dung bình luận.
    private String text;
    // Thời gian hiển thị dạng tương đối.
    private String time;
    // Số lượt thích hiện tại.
    private int likes;
    // true nếu đây là bình luận trả lời.
    private boolean isReply;
    // true nếu người dùng hiện tại đã thích bình luận này.
    private boolean liked;
    // ID bình luận cha, bằng 0 nếu là bình luận cấp cao nhất.
    private long parentCommentId;

    /** Khởi tạo đầy đủ dữ liệu bình luận để hiển thị trên CommentAdapter. */
    public Comment(long id, String name, String text, String time, int likes, boolean isReply, long parentCommentId) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.time = time;
        this.likes = likes;
        this.isReply = isReply;
        this.liked = false;
        this.parentCommentId = parentCommentId;
    }

    // Constructor tương thích với code cũ, mặc định chưa có parentCommentId.
    public Comment(long id, String name, String text, String time, int likes, boolean isReply) {
        this(id, name, text, time, likes, isReply, 0);
    }

    /** Constructor rút gọn cho dữ liệu demo hoặc bình luận chưa có id trong database. */
    public Comment(String name, String text, String time, int likes, boolean isReply) {
        this(0, name, text, time, likes, isReply, 0);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public String getText() { return text; }
    public String getTime() { return time; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public boolean isReply() { return isReply; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(long parentCommentId) { this.parentCommentId = parentCommentId; }
}

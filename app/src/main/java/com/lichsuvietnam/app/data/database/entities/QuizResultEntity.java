package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity Room lưu kết quả một lần làm quiz.
 * Kết quả này dùng để tính điểm, thống kê học tập và lịch sử làm bài.
 */
@Entity(tableName = "quiz_results")
public class QuizResultEntity {
    // Khóa chính tự tăng của kết quả quiz.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng làm bài; bằng 0 nếu là khách.
    public long userId;
    // Số câu trả lời đúng.
    public int correctCount;
    // Tổng số câu hỏi trong bài.
    public int totalQuestions;
    // Thời gian làm bài tính bằng giây.
    public int timeSeconds;
    // Điểm nhận được từ bài quiz.
    public int pointsEarned;
    // Thời điểm tạo kết quả.
    public long createdAt;

    /** Khởi tạo kết quả quiz và tự tính điểm theo số câu đúng. */
    public QuizResultEntity(long userId, int correctCount, int totalQuestions, int timeSeconds) {
        this.userId = userId;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.timeSeconds = timeSeconds;
        this.pointsEarned = correctCount; // Mỗi câu đúng được 1 điểm.
        this.createdAt = System.currentTimeMillis();
    }
}

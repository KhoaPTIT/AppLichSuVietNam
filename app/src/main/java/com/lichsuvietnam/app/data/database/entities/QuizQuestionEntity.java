package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu một câu hỏi quiz.
 * Câu hỏi có thể gắn với sự kiện, nhân vật, lộ trình hoặc bài học cụ thể
 * để QuizActivity tải đúng ngữ cảnh.
 */
@Entity(tableName = "quiz_questions")
public class QuizQuestionEntity {
    // Khóa chính tự tăng của câu hỏi.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // ID sự kiện liên quan, bằng 0 nếu câu hỏi không gắn với sự kiện.
    public long eventId;
    // Nội dung câu hỏi.
    @NonNull public String question;
    // Bốn lựa chọn trả lời.
    @NonNull public String optionA;
    @NonNull public String optionB;
    @NonNull public String optionC;
    @NonNull public String optionD;
    public int correctIndex; // Đáp án đúng: 0=A, 1=B, 2=C, 3=D.
    public String difficulty; // Độ khó: "easy", "medium", "hard".
    public long pathId; // ID lộ trình học.
    public long lessonId; // ID bài học cụ thể.
    public long figureId; // ID nhân vật lịch sử, bằng 0 nếu không gắn nhân vật.

    /** Khởi tạo câu hỏi quiz với 4 lựa chọn và index đáp án đúng. */
    public QuizQuestionEntity(@NonNull String question, @NonNull String optionA,
            @NonNull String optionB, @NonNull String optionC, @NonNull String optionD,
            int correctIndex) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctIndex = correctIndex;
    }
}

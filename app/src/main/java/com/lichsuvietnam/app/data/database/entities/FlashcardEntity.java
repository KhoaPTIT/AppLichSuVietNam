package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity Room lưu một flashcard ôn tập.
 * Mỗi thẻ gồm câu hỏi, đáp án và có thể liên kết với một sự kiện lịch sử.
 */
@Entity(tableName = "flashcards")
public class FlashcardEntity {
    // Khóa chính tự tăng của flashcard.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Mặt trước của thẻ: câu hỏi.
    @NonNull public String question;
    // Mặt sau của thẻ: đáp án.
    @NonNull public String answer;
    // Chủ đề hoặc nhóm kiến thức của thẻ.
    public String category;
    // Sự kiện liên quan, bằng 0 nếu không gắn sự kiện cụ thể.
    public long eventId;

    /** Khởi tạo flashcard với câu hỏi và đáp án. */
    public FlashcardEntity(@NonNull String question, @NonNull String answer) {
        this.question = question;
        this.answer = answer;
    }
}

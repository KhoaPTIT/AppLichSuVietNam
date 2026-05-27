package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity Room lưu tiến độ ôn một flashcard của người dùng.
 * Index unique userId + flashcardId giúp mỗi người dùng chỉ có một trạng thái
 * cho mỗi thẻ.
 */
@Entity(tableName = "flashcard_progress",
        indices = {@Index(value = {"userId", "flashcardId"}, unique = true)})
public class FlashcardProgressEntity {
    // Khóa chính tự tăng của tiến độ flashcard.
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Người dùng sở hữu tiến độ.
    public long userId;
    // Flashcard được theo dõi.
    public long flashcardId;
    // true nếu người dùng đã nhớ thẻ.
    public boolean remembered;
    // Số lần đã ôn thẻ.
    public int reviewCount;
    // Thời điểm ôn gần nhất.
    public long lastReviewedAt;

    /** Khởi tạo tiến độ flashcard với trạng thái chưa nhớ. */
    public FlashcardProgressEntity(long userId, long flashcardId) {
        this.userId = userId;
        this.flashcardId = flashcardId;
        this.remembered = false;
        this.reviewCount = 0;
        this.lastReviewedAt = System.currentTimeMillis();
    }
}

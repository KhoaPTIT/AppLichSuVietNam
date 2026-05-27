package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.FlashcardEntity;
import com.lichsuvietnam.app.data.database.entities.FlashcardProgressEntity;
import java.util.List;

/**
 * DAO Room cho chức năng flashcard trong chế độ học tập tương tác.
 * Quản lý bộ thẻ ôn tập và tiến độ ghi nhớ của từng người dùng.
 */
@Dao
public interface FlashcardDao {
    /** Thêm một flashcard mới. */
    @Insert
    long insert(FlashcardEntity flashcard);

    /** Thêm danh sách flashcard khi seed dữ liệu ban đầu. */
    @Insert
    void insertAll(List<FlashcardEntity> flashcards);

    /** Lấy toàn bộ flashcard dạng LiveData. */
    @Query("SELECT * FROM flashcards")
    LiveData<List<FlashcardEntity>> getAllFlashcards();

    /** Lấy toàn bộ flashcard dạng đồng bộ trong background thread. */
    @Query("SELECT * FROM flashcards")
    List<FlashcardEntity> getAllFlashcardsSync();

    /** Lấy ngẫu nhiên các thẻ người dùng chưa nhớ để ưu tiên ôn lại. */
    @Query("SELECT f.* FROM flashcards f LEFT JOIN flashcard_progress fp ON f.id = fp.flashcardId AND fp.userId = :userId WHERE fp.remembered IS NULL OR fp.remembered = 0 ORDER BY RANDOM() LIMIT :limit")
    List<FlashcardEntity> getUnmasteredCards(long userId, int limit);

    /** Đếm tổng số flashcard. */
    @Query("SELECT COUNT(*) FROM flashcards")
    int getCardCount();

    /** Lưu hoặc thay thế tiến độ của một flashcard. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(FlashcardProgressEntity progress);

    /** Lấy tiến độ ôn của người dùng với một flashcard. */
    @Query("SELECT * FROM flashcard_progress WHERE userId = :userId AND flashcardId = :flashcardId LIMIT 1")
    FlashcardProgressEntity getProgress(long userId, long flashcardId);

    /** Cập nhật trạng thái nhớ/chưa nhớ và số lần ôn của flashcard. */
    @Query("UPDATE flashcard_progress SET remembered = :remembered, reviewCount = reviewCount + 1, lastReviewedAt = :timestamp WHERE userId = :userId AND flashcardId = :flashcardId")
    void updateProgress(long userId, long flashcardId, boolean remembered, long timestamp);

    /** Đếm số thẻ người dùng đã nhớ. */
    @Query("SELECT COUNT(*) FROM flashcard_progress WHERE userId = :userId AND remembered = 1")
    int getMasteredCount(long userId);

    /** Đếm số thẻ người dùng đã từng ôn. */
    @Query("SELECT COUNT(*) FROM flashcard_progress WHERE userId = :userId")
    int getReviewedCount(long userId);
}

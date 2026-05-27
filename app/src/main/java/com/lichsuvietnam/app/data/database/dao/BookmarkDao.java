package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.BookmarkEntity;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.List;

/**
 * DAO Room cho chức năng lưu trữ mục yêu thích.
 * Bookmark liên kết userId với eventId để mỗi người dùng có danh sách sự kiện đã lưu riêng.
 */
@Dao
public interface BookmarkDao {
    /** Lưu một bookmark mới cho người dùng. */
    @Insert
    long insert(BookmarkEntity bookmark);

    /** Bỏ lưu sự kiện khỏi danh sách yêu thích của người dùng. */
    @Query("DELETE FROM bookmarks WHERE userId = :userId AND eventId = :eventId")
    void delete(long userId, long eventId);

    /** Kiểm tra đồng bộ xem sự kiện đã được lưu hay chưa. */
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND eventId = :eventId)")
    boolean isBookmarked(long userId, long eventId);

    /** Kiểm tra dạng LiveData để UI có thể tự cập nhật trạng thái lưu. */
    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE userId = :userId AND eventId = :eventId)")
    LiveData<Boolean> isBookmarkedLive(long userId, long eventId);

    /** Lấy danh sách sự kiện đã lưu, join bookmarks với history_events. */
    @Query("SELECT e.* FROM history_events e INNER JOIN bookmarks b ON e.id = b.eventId WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    LiveData<List<HistoryEventEntity>> getBookmarkedEvents(long userId);

    /** Lấy danh sách tên bộ sưu tập bookmark của người dùng. */
    @Query("SELECT DISTINCT collectionName FROM bookmarks WHERE userId = :userId")
    LiveData<List<String>> getCollections(long userId);

    /** Đếm tổng số bookmark của người dùng. */
    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId")
    LiveData<Integer> getBookmarkCount(long userId);

    /** Đếm số bookmark trong một bộ sưu tập cụ thể. */
    @Query("SELECT COUNT(*) FROM bookmarks WHERE userId = :userId AND collectionName = :collection")
    int getCollectionCount(long userId, String collection);
}

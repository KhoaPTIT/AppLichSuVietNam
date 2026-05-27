package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.SearchHistoryEntity;
import java.util.List;

/**
 * DAO Room cho lịch sử tìm kiếm.
 * SearchActivity dùng lớp này để lưu và đọc lại các từ khóa gần đây của người dùng.
 */
@Dao
public interface SearchHistoryDao {
    /** Lưu một truy vấn tìm kiếm mới. */
    @Insert
    long insert(SearchHistoryEntity history);

    /** Lấy tối đa 10 từ khóa gần đây dạng LiveData để UI tự cập nhật. */
    @Query("SELECT DISTINCT query FROM search_history WHERE userId = :userId ORDER BY createdAt DESC LIMIT 10")
    LiveData<List<String>> getRecentSearches(long userId);

    /** Lấy lịch sử tìm kiếm dạng đồng bộ trong background thread. */
    @Query("SELECT DISTINCT query FROM search_history WHERE userId = :userId ORDER BY createdAt DESC LIMIT 10")
    List<String> getRecentSearchesSync(long userId);

    /** Xóa toàn bộ lịch sử tìm kiếm của một người dùng. */
    @Query("DELETE FROM search_history WHERE userId = :userId")
    void clearHistory(long userId);

    /** Xóa một từ khóa cụ thể khỏi lịch sử tìm kiếm. */
    @Query("DELETE FROM search_history WHERE userId = :userId AND query = :query")
    void deleteQuery(long userId, String query);
}

package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.HistoryEventEntity;
import java.util.List;

/**
 * DAO Room quản lý dữ liệu sự kiện lịch sử.
 * Các truy vấn trong lớp này phục vụ tìm kiếm/lọc sự kiện, chi tiết sự kiện,
 * thư viện ảnh, bản đồ thời gian động và danh sách sự kiện.
 */
@Dao
public interface HistoryEventDao {
    /** Thêm một sự kiện lịch sử mới vào database. */
    @Insert
    long insert(HistoryEventEntity event);

    /** Thêm nhiều sự kiện lịch sử, dùng khi seed dữ liệu ban đầu. */
    @Insert
    void insertAll(List<HistoryEventEntity> events);

    /** Cập nhật thông tin một sự kiện. */
    @Update
    void update(HistoryEventEntity event);

    /** Xóa một sự kiện theo entity. */
    @Delete
    void delete(HistoryEventEntity event);

    /** Xóa sự kiện theo id. */
    @Query("DELETE FROM history_events WHERE id = :id")
    void deleteById(long id);

    /** Lấy toàn bộ sự kiện dạng LiveData, sắp xếp theo năm để UI tự cập nhật. */
    @Query("SELECT * FROM history_events ORDER BY yearNumeric ASC")
    LiveData<List<HistoryEventEntity>> getAllEvents();

    /** Lấy toàn bộ sự kiện dạng đồng bộ, dùng trong background thread. */
    @Query("SELECT * FROM history_events ORDER BY yearNumeric ASC")
    List<HistoryEventEntity> getAllEventsSync();

    /** Lấy chi tiết một sự kiện dạng LiveData cho EventDetailActivity. */
    @Query("SELECT * FROM history_events WHERE id = :id LIMIT 1")
    LiveData<HistoryEventEntity> getEventById(long id);

    /** Lấy chi tiết một sự kiện dạng đồng bộ khi cần xử lý ngoài UI thread. */
    @Query("SELECT * FROM history_events WHERE id = :id LIMIT 1")
    HistoryEventEntity getEventByIdSync(long id);

    /** Lọc sự kiện theo giai đoạn lịch sử. */
    @Query("SELECT * FROM history_events WHERE period = :period ORDER BY yearNumeric ASC")
    LiveData<List<HistoryEventEntity>> getEventsByPeriod(String period);

    /** Lọc sự kiện theo danh mục. */
    @Query("SELECT * FROM history_events WHERE category = :category ORDER BY yearNumeric ASC")
    LiveData<List<HistoryEventEntity>> getEventsByCategory(String category);

    /** Tìm kiếm sự kiện theo tiêu đề, mô tả, địa điểm hoặc nhân vật liên quan. */
    @Query("SELECT * FROM history_events WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' OR keyFigures LIKE '%' || :query || '%' ORDER BY yearNumeric ASC")
    LiveData<List<HistoryEventEntity>> searchEvents(String query);

    /** Tìm kiếm đồng bộ, dùng khi đang chạy trong background thread. */
    @Query("SELECT * FROM history_events WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY yearNumeric ASC")
    List<HistoryEventEntity> searchEventsSync(String query);

    /** Đếm tổng số sự kiện để hiển thị thống kê. */
    @Query("SELECT COUNT(*) FROM history_events")
    int getCount();
}

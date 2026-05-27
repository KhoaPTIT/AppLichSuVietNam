package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.VideoEntity;
import java.util.List;

/**
 * DAO Room cho chức năng Video & tài liệu đa phương tiện.
 * Cung cấp truy vấn video theo sự kiện, toàn bộ video và video liên quan.
 */
@Dao
public interface VideoDao {
    /** Thêm danh sách video khi seed dữ liệu ban đầu. */
    @Insert
    void insertAll(List<VideoEntity> videos);

    /** Lấy video liên quan tới một sự kiện dạng LiveData để UI tự cập nhật. */
    @Query("SELECT * FROM videos WHERE eventId = :eventId")
    LiveData<List<VideoEntity>> getVideosByEvent(long eventId);

    /** Lấy video theo sự kiện dạng đồng bộ trong background thread. */
    @Query("SELECT * FROM videos WHERE eventId = :eventId")
    List<VideoEntity> getVideosByEventSync(long eventId);

    /** Lấy toàn bộ video cho màn hình danh sách video. */
    @Query("SELECT * FROM videos")
    LiveData<List<VideoEntity>> getAllVideos();

    /** Lấy toàn bộ video dạng đồng bộ, dùng khi tính video liên quan. */
    @Query("SELECT * FROM videos")
    List<VideoEntity> getAllVideosSync();

    /** Lấy tối đa 5 video liên quan, loại trừ video hiện tại. */
    @Query("SELECT * FROM videos WHERE id != :excludeId LIMIT 5")
    List<VideoEntity> getRelatedVideosSync(long excludeId);

    /** Đếm tổng số video trong database. */
    @Query("SELECT COUNT(*) FROM videos")
    int getCount();
}

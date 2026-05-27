package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.LearningPathEntity;
import com.lichsuvietnam.app.data.database.entities.LearningProgressEntity;
import com.lichsuvietnam.app.data.database.entities.LessonEntity;
import com.lichsuvietnam.app.data.database.entities.LessonProgressEntity;
import java.util.List;

/**
 * DAO Room cho chế độ học tập tương tác.
 * Quản lý lộ trình học, bài học, tiến độ cấp lộ trình và tiến độ từng bài.
 */
@Dao
public interface LearningDao {
    // Nhóm truy vấn lộ trình học.
    /** Thêm một lộ trình học mới. */
    @Insert
    long insertPath(LearningPathEntity path);

    /** Thêm danh sách lộ trình học khi seed dữ liệu ban đầu. */
    @Insert
    void insertAllPaths(List<LearningPathEntity> paths);

    /** Lấy toàn bộ lộ trình dạng LiveData để UI tự cập nhật. */
    @Query("SELECT * FROM learning_paths ORDER BY orderIndex ASC")
    LiveData<List<LearningPathEntity>> getAllPaths();

    /** Lấy toàn bộ lộ trình dạng đồng bộ trong background thread. */
    @Query("SELECT * FROM learning_paths ORDER BY orderIndex ASC")
    List<LearningPathEntity> getAllPathsSync();

    /** Lấy một lộ trình theo id. */
    @Query("SELECT * FROM learning_paths WHERE id = :pathId LIMIT 1")
    LearningPathEntity getPathById(long pathId);

    /** Đếm số lộ trình học trong database. */
    @Query("SELECT COUNT(*) FROM learning_paths")
    int getPathCount();

    // Nhóm tiến độ cấp lộ trình.
    /** Thêm hoặc thay thế tiến độ học của một lộ trình. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(LearningProgressEntity progress);

    /** Lấy tiến độ học của người dùng trong một lộ trình. */
    @Query("SELECT * FROM learning_progress WHERE userId = :userId AND pathId = :pathId LIMIT 1")
    LearningProgressEntity getProgress(long userId, long pathId);

    /** Lấy toàn bộ tiến độ lộ trình của một người dùng. */
    @Query("SELECT * FROM learning_progress WHERE userId = :userId")
    LiveData<List<LearningProgressEntity>> getAllProgress(long userId);

    /** Tăng số bài đã hoàn thành ở cấp lộ trình. */
    @Query("UPDATE learning_progress SET completedLessons = completedLessons + 1, lastAccessedAt = :timestamp WHERE userId = :userId AND pathId = :pathId")
    void incrementLesson(long userId, long pathId, long timestamp);

    /** Tính tổng số bài đã hoàn thành của người dùng trên tất cả lộ trình. */
    @Query("SELECT SUM(completedLessons) FROM learning_progress WHERE userId = :userId")
    int getTotalCompletedLessons(long userId);

    /** Tính tổng số bài học trong toàn bộ hệ thống. */
    @Query("SELECT SUM(lp.totalLessons) FROM learning_paths lp")
    int getTotalLessons();

    // Nhóm truy vấn bài học.
    /** Thêm một bài học mới. */
    @Insert
    long insertLesson(LessonEntity lesson);

    /** Thêm danh sách bài học khi seed dữ liệu ban đầu. */
    @Insert
    void insertAllLessons(List<LessonEntity> lessons);

    /** Lấy các bài học của một lộ trình dạng đồng bộ. */
    @Query("SELECT * FROM lessons WHERE pathId = :pathId ORDER BY orderIndex ASC")
    List<LessonEntity> getLessonsByPathSync(long pathId);

    /** Lấy các bài học của một lộ trình dạng LiveData. */
    @Query("SELECT * FROM lessons WHERE pathId = :pathId ORDER BY orderIndex ASC")
    LiveData<List<LessonEntity>> getLessonsByPath(long pathId);

    /** Lấy một bài học theo id. */
    @Query("SELECT * FROM lessons WHERE id = :lessonId LIMIT 1")
    LessonEntity getLessonById(long lessonId);

    /** Đếm tổng số bài học trong database. */
    @Query("SELECT COUNT(*) FROM lessons")
    int getLessonCount();

    /** Đếm số bài học trong một lộ trình. */
    @Query("SELECT COUNT(*) FROM lessons WHERE pathId = :pathId")
    int getLessonCountByPath(long pathId);

    // Nhóm tiến độ từng bài học.
    /** Thêm hoặc thay thế tiến độ của một bài học. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLessonProgress(LessonProgressEntity progress);

    /** Lấy tiến độ của người dùng trong một bài học. */
    @Query("SELECT * FROM lesson_progress WHERE userId = :userId AND lessonId = :lessonId LIMIT 1")
    LessonProgressEntity getLessonProgress(long userId, long lessonId);

    /** Đếm số bài đã hoàn thành của người dùng trong một lộ trình. */
    @Query("SELECT COUNT(*) FROM lesson_progress WHERE userId = :userId AND lessonId IN (SELECT id FROM lessons WHERE pathId = :pathId) AND completed = 1")
    int getCompletedLessonCountByPath(long userId, long pathId);

    /** Đồng bộ tiến độ cấp lộ trình từ số bài học đã hoàn thành thực tế. */
    @Query("UPDATE learning_progress SET completedLessons = " +
           "(SELECT COUNT(*) FROM lesson_progress WHERE userId = :userId AND lessonId IN " +
           "(SELECT id FROM lessons WHERE pathId = :pathId) AND completed = 1), " +
           "lastAccessedAt = :timestamp WHERE userId = :userId AND pathId = :pathId")
    void syncPathProgress(long userId, long pathId, long timestamp);
}

package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.QuizQuestionEntity;
import com.lichsuvietnam.app.data.database.entities.QuizResultEntity;
import java.util.List;

/**
 * DAO Room cho chức năng Câu hỏi & bài kiểm tra.
 * Quản lý ngân hàng câu hỏi, truy vấn câu hỏi theo ngữ cảnh và lưu kết quả làm bài.
 */
@Dao
public interface QuizDao {
    /** Thêm một câu hỏi vào ngân hàng câu hỏi. */
    @Insert
    long insertQuestion(QuizQuestionEntity question);

    /** Thêm danh sách câu hỏi khi seed dữ liệu ban đầu. */
    @Insert
    void insertAllQuestions(List<QuizQuestionEntity> questions);

    /** Lấy ngẫu nhiên một số câu hỏi bất kỳ. */
    @Query("SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT :limit")
    List<QuizQuestionEntity> getRandomQuestions(int limit);

    /** Lấy câu hỏi gắn với một sự kiện lịch sử. */
    @Query("SELECT * FROM quiz_questions WHERE eventId = :eventId ORDER BY RANDOM()")
    List<QuizQuestionEntity> getQuestionsByEvent(long eventId);

    /** Lấy câu hỏi gắn với một nhân vật lịch sử. */
    @Query("SELECT * FROM quiz_questions WHERE figureId = :figureId ORDER BY RANDOM()")
    List<QuizQuestionEntity> getQuestionsByFigure(long figureId);

    /** Lấy câu hỏi theo lộ trình học. */
    @Query("SELECT * FROM quiz_questions WHERE pathId = :pathId ORDER BY RANDOM()")
    List<QuizQuestionEntity> getQuestionsByPath(long pathId);

    /** Lấy câu hỏi theo bài học cụ thể. */
    @Query("SELECT * FROM quiz_questions WHERE lessonId = :lessonId ORDER BY RANDOM()")
    List<QuizQuestionEntity> getQuestionsByLesson(long lessonId);

    /** Đếm số câu hỏi của một bài học. */
    @Query("SELECT COUNT(*) FROM quiz_questions WHERE lessonId = :lessonId")
    int getQuestionCountByLesson(long lessonId);

    /** Lấy ngẫu nhiên câu hỏi cho bài kiểm tra tổng hợp. */
    @Query("SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT :limit")
    List<QuizQuestionEntity> getAllQuestionsRandom(int limit);

    /** Lấy câu hỏi theo độ khó. */
    @Query("SELECT * FROM quiz_questions WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    List<QuizQuestionEntity> getQuestionsByDifficulty(String difficulty, int limit);

    /** Đếm tổng số câu hỏi trong ngân hàng câu hỏi. */
    @Query("SELECT COUNT(*) FROM quiz_questions")
    int getQuestionCount();

    /** Đếm số câu hỏi theo sự kiện. */
    @Query("SELECT COUNT(*) FROM quiz_questions WHERE eventId = :eventId")
    int getQuestionCountByEvent(long eventId);

    /** Đếm số câu hỏi theo nhân vật. */
    @Query("SELECT COUNT(*) FROM quiz_questions WHERE figureId = :figureId")
    int getQuestionCountByFigure(long figureId);

    /** Đếm số câu hỏi theo lộ trình học. */
    @Query("SELECT COUNT(*) FROM quiz_questions WHERE pathId = :pathId")
    int getQuestionCountByPath(long pathId);

    /** Lưu kết quả một lần làm quiz. */
    @Insert
    long insertResult(QuizResultEntity result);

    /** Lấy lịch sử kết quả quiz của người dùng dạng LiveData. */
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<QuizResultEntity>> getResultsByUser(long userId);

    /** Lấy lịch sử kết quả quiz dạng đồng bộ trong background thread. */
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY createdAt DESC")
    List<QuizResultEntity> getResultsByUserSync(long userId);

    /** Lấy kết quả quiz gần nhất của người dùng. */
    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
    QuizResultEntity getLatestResult(long userId);

    /** Tính tổng điểm người dùng kiếm được từ quiz. */
    @Query("SELECT SUM(pointsEarned) FROM quiz_results WHERE userId = :userId")
    int getTotalPoints(long userId);

    /** Tính điểm trung bình theo phần trăm đúng. */
    @Query("SELECT AVG(correctCount * 100.0 / totalQuestions) FROM quiz_results WHERE userId = :userId")
    float getAverageScore(long userId);
}

package com.lichsuvietnam.app.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.lichsuvietnam.app.data.database.dao.*;
import com.lichsuvietnam.app.data.database.entities.*;

@Database(entities = {
    UserEntity.class,
    HistoryEventEntity.class,
    QuizQuestionEntity.class,
    QuizResultEntity.class,
    FlashcardEntity.class,
    FlashcardProgressEntity.class,
    LearningPathEntity.class,
    LearningProgressEntity.class,
    LessonEntity.class,
    LessonProgressEntity.class,
    PostEntity.class,
    CommentEntity.class,
    BookmarkEntity.class,
    SearchHistoryEntity.class,
    HistoricalFigureEntity.class,
    VideoEntity.class,
    NotificationEntity.class,
    PostLikeEntity.class,
    CommentLikeEntity.class
}, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract HistoryEventDao historyEventDao();
    public abstract QuizDao quizDao();
    public abstract FlashcardDao flashcardDao();
    public abstract LearningDao learningDao();
    public abstract CommunityDao communityDao();
    public abstract BookmarkDao bookmarkDao();
    public abstract SearchHistoryDao searchHistoryDao();
    public abstract FigureDao figureDao();
    public abstract VideoDao videoDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "lichsu_vietnam.db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}

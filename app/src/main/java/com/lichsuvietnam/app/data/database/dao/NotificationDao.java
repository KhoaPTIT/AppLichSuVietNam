package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.NotificationEntity;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    long insert(NotificationEntity notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<NotificationEntity>> getNotifications(long userId);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    List<NotificationEntity> getNotificationsSync(long userId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    LiveData<Integer> getUnreadCount(long userId);

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    int getUnreadCountSync(long userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notifId")
    void markAsRead(long notifId);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsRead(long userId);

    @Query("DELETE FROM notifications WHERE id = :notifId")
    void delete(long notifId);
}

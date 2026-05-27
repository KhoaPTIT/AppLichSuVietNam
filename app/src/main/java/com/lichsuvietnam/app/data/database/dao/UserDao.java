package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.UserEntity;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserById(long id);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<UserEntity> getUserByIdLive(long id);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :passwordHash LIMIT 1")
    UserEntity authenticate(String email, String passwordHash);

    @Query("UPDATE users SET points = points + :points WHERE id = :userId")
    void addPoints(long userId, int points);

    @Query("UPDATE users SET streak = :streak WHERE id = :userId")
    void updateStreak(long userId, int streak);

    @Query("UPDATE users SET hoursLearned = hoursLearned + 1 WHERE id = :userId")
    void incrementHoursLearned(long userId);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int countByEmail(String email);

    // Admin queries
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    LiveData<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    List<UserEntity> getAllUsersSync();

    @Query("SELECT * FROM users WHERE name LIKE '%' || :q || '%' OR email LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    List<UserEntity> searchUsers(String q);

    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();

    @Query("UPDATE users SET role = :role WHERE id = :userId")
    void updateRole(long userId, String role);

    @Query("UPDATE users SET isActive = :active WHERE id = :userId")
    void updateActiveStatus(long userId, boolean active);

    @Query("UPDATE users SET passwordHash = :hash WHERE id = :userId")
    void updatePassword(long userId, String hash);

    @Query("UPDATE users SET points = :pts WHERE id = :userId")
    void setPoints(long userId, int pts);
}

package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String email;

    @NonNull
    public String passwordHash;

    public String avatarUrl;
    public int points = 0;
    public int streak = 0;
    public int hoursLearned = 0;
    public long createdAt;
    @NonNull public String role = "user"; // "user" or "admin"
    public boolean isActive = true;

    public UserEntity(@NonNull String name, @NonNull String email, @NonNull String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = "user";
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
    }
}

package com.lichsuvietnam.app.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "historical_figures")
public class HistoricalFigureEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String name;
    public String title;
    public String birthYear;
    public String deathYear;
    public String dynasty;
    public String period;
    public String biography;
    public String achievements;
    public String imageUrl;
    public String relatedEventIds;
    public boolean isFeatured;

    // New fields for enhanced feature
    public String queQuan;
    public String role;
    public String relatedFigureIds;
    public String timelineMilestones;
    public String shortDesc;
    public boolean isFavorite;

    public HistoricalFigureEntity(@NonNull String name) {
        this.name = name;
    }

    public String formatLifeSpan() {
        if (birthYear != null && deathYear != null) return birthYear + " – " + deathYear;
        if (birthYear != null) return birthYear + " – ?";
        return "Không rõ";
    }
}

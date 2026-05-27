package com.lichsuvietnam.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.lichsuvietnam.app.data.database.entities.HistoricalFigureEntity;
import java.util.List;

@Dao
public interface FigureDao {
    @Insert
    long insert(HistoricalFigureEntity figure);

    @Insert
    void insertAll(List<HistoricalFigureEntity> figures);

    @Update
    void update(HistoricalFigureEntity figure);

    @Delete
    void delete(HistoricalFigureEntity figure);

    @Query("DELETE FROM historical_figures WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM historical_figures ORDER BY name ASC")
    LiveData<List<HistoricalFigureEntity>> getAllFigures();

    @Query("SELECT * FROM historical_figures ORDER BY name ASC")
    List<HistoricalFigureEntity> getAllFiguresSync();

    @Query("SELECT * FROM historical_figures WHERE period = :period ORDER BY name ASC")
    LiveData<List<HistoricalFigureEntity>> getFiguresByPeriod(String period);

    @Query("SELECT * FROM historical_figures WHERE role = :role ORDER BY name ASC")
    LiveData<List<HistoricalFigureEntity>> getFiguresByRole(String role);

    @Query("SELECT * FROM historical_figures WHERE period = :period AND role = :role ORDER BY name ASC")
    LiveData<List<HistoricalFigureEntity>> getFiguresByPeriodAndRole(String period, String role);

    @Query("SELECT * FROM historical_figures WHERE id = :id LIMIT 1")
    HistoricalFigureEntity getFigureByIdSync(long id);

    @Query("SELECT * FROM historical_figures WHERE id = :id LIMIT 1")
    LiveData<HistoricalFigureEntity> getFigureById(long id);

    @Query("SELECT * FROM historical_figures WHERE isFeatured = 1")
    LiveData<List<HistoricalFigureEntity>> getFeaturedFigures();

    @Query("SELECT * FROM historical_figures WHERE isFavorite = 1 ORDER BY name ASC")
    LiveData<List<HistoricalFigureEntity>> getFavoriteFigures();

    @Query("SELECT * FROM historical_figures WHERE name LIKE '%' || :q || '%' OR title LIKE '%' || :q || '%' OR dynasty LIKE '%' || :q || '%' OR role LIKE '%' || :q || '%' OR shortDesc LIKE '%' || :q || '%'")
    List<HistoricalFigureEntity> searchFiguresSync(String q);

    @Query("SELECT * FROM historical_figures WHERE name LIKE '%' || :q || '%' OR title LIKE '%' || :q || '%' OR dynasty LIKE '%' || :q || '%'")
    LiveData<List<HistoricalFigureEntity>> searchFigures(String q);

    @Query("SELECT * FROM historical_figures WHERE id IN (:ids) ORDER BY name ASC")
    List<HistoricalFigureEntity> getFiguresByIdsSync(List<Long> ids);

    @Query("SELECT * FROM historical_figures WHERE period = :period AND id != :excludeId ORDER BY name ASC")
    List<HistoricalFigureEntity> getFiguresByPeriodExcludingSync(String period, long excludeId);

    @Query("SELECT DISTINCT role FROM historical_figures WHERE role IS NOT NULL ORDER BY role ASC")
    List<String> getAllRolesSync();

    @Query("SELECT COUNT(*) FROM historical_figures")
    int getCount();
}

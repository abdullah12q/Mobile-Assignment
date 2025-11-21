package com.example.mobile_assignment.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mobile_assignment.model.ActivityEntity

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: ActivityEntity): Long

    @Query("SELECT * FROM activities ORDER BY id DESC")
    suspend fun getAll(): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE date = :date ORDER BY id DESC")
    suspend fun getByDate(date: String): List<ActivityEntity>

    @Query("SELECT SUM(duration) FROM activities WHERE date = :date")
    suspend fun totalDurationForDate(date: String): Int?
}

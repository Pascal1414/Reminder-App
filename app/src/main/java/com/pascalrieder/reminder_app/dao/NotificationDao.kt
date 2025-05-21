package com.pascalrieder.reminder_app.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pascalrieder.reminder_app.model.Notification


@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notification")
    suspend fun getAll(): List<Notification>

    @Query("SELECT * FROM Notification WHERE reminder_id = :reminderId")
    suspend fun getByReminderId(reminderId: Long): List<Notification>

    @Query(
        """
    SELECT * FROM Notification
    WHERE reminder_id = :reminderId
    AND shownAt BETWEEN :startMillis AND :endMillis
"""
    )
    suspend fun getByReminderIdBetweenTimes(
        reminderId: Long,
        startMillis: Long,
        endMillis: Long
    ): List<Notification>

    @Insert
    suspend fun create(notification: Notification): Long
}
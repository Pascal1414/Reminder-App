package com.pascalrieder.reminder_app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pascalrieder.reminder_app.converter.IntervalConverters
import com.pascalrieder.reminder_app.converter.LocalDateTimeConverters
import com.pascalrieder.reminder_app.converter.LocalTimeConverter
import com.pascalrieder.reminder_app.converter.WeekdayConverters
import com.pascalrieder.reminder_app.dao.NotificationDao
import com.pascalrieder.reminder_app.dao.ReminderCheckDao
import com.pascalrieder.reminder_app.dao.ReminderDao
import com.pascalrieder.reminder_app.model.Notification
import com.pascalrieder.reminder_app.model.Reminder
import com.pascalrieder.reminder_app.model.ReminderCheck

@Database(entities = [Reminder::class, ReminderCheck::class, Notification::class], version = 6)
@TypeConverters(
    LocalDateTimeConverters::class,
    IntervalConverters::class,
    WeekdayConverters::class,
    LocalTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-name"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }

    abstract fun reminderDao(): ReminderDao
    abstract fun reminderCheckDao(): ReminderCheckDao
    abstract fun notificationDao(): NotificationDao

}
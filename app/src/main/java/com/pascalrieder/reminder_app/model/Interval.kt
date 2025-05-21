package com.pascalrieder.reminder_app.model

enum class Interval {
    Daily,
    Weekly;

    companion object {
        fun getStringValues(): List<String> {
            return entries.map { it.name }
        }
    }
}
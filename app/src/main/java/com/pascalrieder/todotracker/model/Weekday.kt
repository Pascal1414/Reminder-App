package com.pascalrieder.todotracker.model

enum class Weekday {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday;

    companion object {
        fun getStringValues(): List<String> {
            return entries.map { it.name }
        }
    }
}
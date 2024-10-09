package com.techglock.health.app.fitness_tracker.util

import java.util.Date

data class WeekDay(
    var dayPosition: Int = 0,
    var dayOfWeek: String = "",
    var dayOfMonth: String = "",
    var dateString: String = "",
    var dateDisplay: String = "",
    var date: Date = Date(),
    var isToday: Boolean = false
)
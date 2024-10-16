package com.techglock.health.app.water_tracker.model

data class CalenderTrackModel(
    var trackDate: String = "",
    var trackWeekDay: String = "",
    var trackMonthYear: String = "",
    var trackDisplayDate: String = "",
    var trackServerDate: String = "",
    var trackGoal: Double = 0.0,
    //var trackGoal: Double = Constants.DEFAULT_WATER_GOAL.toDouble(),
    var trackAchieved: Double = 0.0,
    var trackGoalPercentage: Double = 0.0,
    var isGoalAchieved: Boolean = false,
)
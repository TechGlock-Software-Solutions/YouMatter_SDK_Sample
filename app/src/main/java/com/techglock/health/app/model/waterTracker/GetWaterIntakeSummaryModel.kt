package com.techglock.health.app.model.waterTracker

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetWaterIntakeSummaryModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("Request")
        @Expose
        var Request: Request = Request()
    )

    data class Request(
        @SerializedName("PersonID")
        @Expose
        val personID: Int = 0,
        @SerializedName("FromDate")
        @Expose
        val fromDate: String = "",
        @SerializedName("ToDate")
        @Expose
        val toDate: String = ""
    )

    data class GetWaterIntakeSummaryResponse(
        @SerializedName("Result")
        @Expose
        var result: Result = Result()
    )

    data class Result(
        @SerializedName("WeeklyWaterIntake")
        @Expose
        val weeklyWaterIntake: WeeklyWaterIntake = WeeklyWaterIntake(),
        @SerializedName("LifeTimeWaterIntake")
        @Expose
        val lifeTimeWaterIntake: LifeTimeWaterIntake = LifeTimeWaterIntake(),
        @SerializedName("WaterStreak")
        @Expose
        val waterStreak: WaterStreak = WaterStreak()
    )

    data class WeeklyWaterIntake(
        @SerializedName("Result")
        val result: List<WeeklyWaterIntakeResult> = listOf()
    )

    data class LifeTimeWaterIntake(
        @SerializedName("Result")
        @Expose
        val result: List<LifetimeResult> = listOf()
    )

    data class WaterStreak(
        @SerializedName("Result")
        val result: List<StreakResult> = listOf()
    )

    data class WeeklyWaterIntakeResult(
        @SerializedName("WeekTillDate")
        @Expose
        val weekTillDate: String = "",
        @SerializedName("AchievementThisWeek")
        @Expose
        val achievementThisWeek: String = "",
        /*        @SerializedName("ID")
                @Expose
                val id: Int = 0,
                @SerializedName("TotalGoal")
                @Expose
                val totalGoal: String = "",
                @SerializedName("TotalIntake")
                @Expose
                val totalIntake: String = "",
                @SerializedName("CompletedGoalPercentage")
                @Expose
                val completedGoalPercentage: Double = 0.0*/
    )

    data class LifetimeResult(
        @SerializedName("PersonID")
        @Expose
        val personID: Int = 0,
        @SerializedName("LifeTimeWaterIntake")
        @Expose
        val lifeTimeWaterIntake: Double = 0.0
    )

    data class StreakResult(
        @SerializedName("PersonID")
        @Expose
        val personID: Int = 0,
        @SerializedName("CurrentStreak")
        @Expose
        val currentStreak: Int = 0,
        @SerializedName("MaximumStreak")
        @Expose
        val maximumStreak: Int = 0,
        @SerializedName("MaxStreak")
        @Expose
        val maxStreak: Int = 0,
        @SerializedName("TotalStreak")
        @Expose
        val totalStreak: Int = 0
    )

}

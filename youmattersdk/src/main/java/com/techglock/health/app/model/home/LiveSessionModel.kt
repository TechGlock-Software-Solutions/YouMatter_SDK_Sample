package com.techglock.health.app.model.home

import com.google.gson.annotations.SerializedName

data class LiveSessionModel(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("schedule")
    val schedule: String = "",
    @SerializedName("time")
    val time: String = "",
    @SerializedName("day")
    val day: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("link")
    val link: String = "",
    @SerializedName("bannerImage")
    val bannerImage: String = "",
    @SerializedName("durationInMinutes")
    val durationInMinutes: Int = 0,
    @SerializedName("participantSource")
    val participantSource: Any = Any(),
    @SerializedName("removable")
    val removable: Boolean = false
)
package com.techglock.health.app.home.di

import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.TrackParameterMaster

interface ScoreListener {
    fun onScore(hraSummary: HRASummary?)
    fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>)
}
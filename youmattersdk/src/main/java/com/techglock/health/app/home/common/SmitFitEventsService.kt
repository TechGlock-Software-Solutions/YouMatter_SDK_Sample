package com.techglock.health.app.home.common

import com.techglock.health.app.model.home.LiveSessionModel
import retrofit2.Call
import retrofit2.http.GET

interface SmitFitEventsService {

    @GET("microservice_v3/event/v2/")
    fun getSmitFitEventsApi(): Call<List<LiveSessionModel>>

}
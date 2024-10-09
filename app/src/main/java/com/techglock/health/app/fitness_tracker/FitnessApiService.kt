package com.techglock.health.app.fitness_tracker


import com.techglock.health.app.common.constants.Constants
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FitnessApiService {

    @POST(Constants.FITNESS_LIST_HISTORY_API)
    suspend fun getStepsHistoryApi(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST(Constants.FITNESS_STEP_SAVE_LIST_API)
    suspend fun saveStepsListApi(@Body requestBody: RequestBody): Response<ResponseBody>

}
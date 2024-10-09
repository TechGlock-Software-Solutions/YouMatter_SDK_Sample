package com.techglock.health.app.hra

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface DownloadReportApiService {
    @GET
    fun downloadHraReport(@Url fileUrl: String): Call<ResponseBody>
}
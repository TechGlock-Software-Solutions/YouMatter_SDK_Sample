package com.techglock.health.app.home.common

import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import retrofit2.Call
import retrofit2.http.*

interface SudPolicyApiService {
    @POST(Constants.SUD_CUSTOMER_POLICY_SERVICE_API)
    fun getSudKyp(@Body request: SudKYPModel): Call<SudKYPModel.SudKYPResponse>

}
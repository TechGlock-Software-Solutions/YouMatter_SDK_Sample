package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.model.aktivo.AktivoCheckUserModel
import com.techglock.health.app.model.aktivo.AktivoCreateUserModel
import com.techglock.health.app.model.aktivo.AktivoGetRefreshTokenModel
import com.techglock.health.app.model.aktivo.AktivoGetUserModel
import com.techglock.health.app.model.aktivo.AktivoGetUserTokenModel
import com.techglock.health.app.remote.AktivoDatasource
import com.techglock.health.app.repository.utils.NetworkDataBoundResource
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

interface AktivoRepository {

    suspend fun aktivoCheckUser(data: AktivoCheckUserModel): LiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>>

    suspend fun aktivoCreateUser(data: AktivoCreateUserModel): LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>>

    suspend fun aktivoGetUserToken(data: AktivoGetUserTokenModel): LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>>

    suspend fun aktivoGetRefreshToken(data: AktivoGetRefreshTokenModel): LiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>>

    suspend fun aktivoGetUser(data: AktivoGetUserModel): LiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>>
}


class AktivoRepositoryImpl @Inject constructor(
    private val datasource: AktivoDatasource,
    val context: Context
) :
    AktivoRepository {

    override suspend fun aktivoCheckUser(data: AktivoCheckUserModel): LiveData<Resource<AktivoCheckUserModel.AktivoCheckUserResponse>> {

        return object :
            NetworkDataBoundResource<AktivoCheckUserModel.AktivoCheckUserResponse, AktivoCheckUserModel.AktivoCheckUserResponse>(
                context
            ) {

            override fun processResponse(response: AktivoCheckUserModel.AktivoCheckUserResponse): AktivoCheckUserModel.AktivoCheckUserResponse {
                return response
            }

            override suspend fun createCallAsync(): AktivoCheckUserModel.AktivoCheckUserResponse {
                return datasource.aktivoCheckUser(data)
            }

        }.build().asLiveData()

    }

    override suspend fun aktivoCreateUser(data: AktivoCreateUserModel): LiveData<Resource<AktivoCreateUserModel.AktivoCreateUserResponse>> {

        return object :
            NetworkDataBoundResource<AktivoCreateUserModel.AktivoCreateUserResponse, AktivoCreateUserModel.AktivoCreateUserResponse>(
                context
            ) {
            override fun processResponse(response: AktivoCreateUserModel.AktivoCreateUserResponse): AktivoCreateUserModel.AktivoCreateUserResponse {
                return response
            }

            override suspend fun createCallAsync(): AktivoCreateUserModel.AktivoCreateUserResponse {
                return datasource.aktivoCreateUser(data)
            }

        }.build().asLiveData()

    }

    override suspend fun aktivoGetUserToken(data: AktivoGetUserTokenModel): LiveData<Resource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse>> {

        return object :
            NetworkDataBoundResource<AktivoGetUserTokenModel.AktivoGetUserTokenResponse, AktivoGetUserTokenModel.AktivoGetUserTokenResponse>(
                context
            ) {

            override fun processResponse(response: AktivoGetUserTokenModel.AktivoGetUserTokenResponse): AktivoGetUserTokenModel.AktivoGetUserTokenResponse {
                return response
            }

            override suspend fun createCallAsync(): AktivoGetUserTokenModel.AktivoGetUserTokenResponse {
                return datasource.aktivoGetUserToken(data)
            }


        }.build().asLiveData()

    }

    override suspend fun aktivoGetRefreshToken(data: AktivoGetRefreshTokenModel): LiveData<Resource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>> {

        return object :
            NetworkDataBoundResource<AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse, AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse>(
                context
            ) {

            override fun processResponse(response: AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse): AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse {
                return response
            }

            override suspend fun createCallAsync(): AktivoGetRefreshTokenModel.AktivoGetRefreshTokenResponse {
                return datasource.aktivoGetRefreshToken(data)
            }

        }.build().asLiveData()

    }

    override suspend fun aktivoGetUser(data: AktivoGetUserModel): LiveData<Resource<AktivoGetUserModel.AktivoGetUserResponse>> {

        return object :
            NetworkDataBoundResource<AktivoGetUserModel.AktivoGetUserResponse, AktivoGetUserModel.AktivoGetUserResponse>(
                context
            ) {
            override fun processResponse(response: AktivoGetUserModel.AktivoGetUserResponse): AktivoGetUserModel.AktivoGetUserResponse {
                return response
            }

            override suspend fun createCallAsync(): AktivoGetUserModel.AktivoGetUserResponse {
                return datasource.aktivoGetUser(data)
            }

        }.build().asLiveData()

    }

}
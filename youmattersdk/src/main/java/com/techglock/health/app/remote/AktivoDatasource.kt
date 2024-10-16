package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.aktivo.AktivoCheckUserModel
import com.techglock.health.app.model.aktivo.AktivoCreateUserModel
import com.techglock.health.app.model.aktivo.AktivoGetRefreshTokenModel
import com.techglock.health.app.model.aktivo.AktivoGetUserModel
import com.techglock.health.app.model.aktivo.AktivoGetUserTokenModel
import javax.inject.Inject
import javax.inject.Named

class AktivoDatasource @Inject constructor(@Named(DIModule.DEFAULT_NEW) private val defaultService: ApiService) {

    suspend fun aktivoCheckUser(data: AktivoCheckUserModel) = defaultService.aktivoCheckUser(data)

    suspend fun aktivoCreateUser(data: AktivoCreateUserModel) =
        defaultService.aktivoCreateUser(data)

    suspend fun aktivoGetUserToken(data: AktivoGetUserTokenModel) =
        defaultService.aktivoGetUserToken(data)

    suspend fun aktivoGetRefreshToken(data: AktivoGetRefreshTokenModel) =
        defaultService.aktivoGetRefreshToken(data)

    suspend fun aktivoGetUser(data: AktivoGetUserModel) = defaultService.aktivoGetUser(data)
}
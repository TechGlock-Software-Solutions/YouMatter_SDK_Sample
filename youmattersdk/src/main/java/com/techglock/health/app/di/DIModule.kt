package com.techglock.health.app.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.fitness_tracker.util.StepCountHelper
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.domain.BackgroundCallUseCase
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.local.ArchAppDatabase
import com.techglock.health.app.local.dao.AppCacheMasterDao
import com.techglock.health.app.local.dao.DataSyncMasterDao
import com.techglock.health.app.local.dao.FitnessDao
import com.techglock.health.app.local.dao.HRADao
import com.techglock.health.app.local.dao.MedicationDao
import com.techglock.health.app.local.dao.StoreRecordsDao
import com.techglock.health.app.local.dao.TrackParameterDao
import com.techglock.health.app.local.dao.VivantUserDao
import com.techglock.health.app.medication_tracker.common.MedicationTrackerHelper
import com.techglock.health.app.medication_tracker.domain.MedicationManagementUseCase
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.remote.AktivoDatasource
import com.techglock.health.app.remote.ApiService
import com.techglock.health.app.remote.BlogsDatasource
import com.techglock.health.app.remote.DecryptionInterceptor
import com.techglock.health.app.remote.FitnessDatasource
import com.techglock.health.app.remote.HomeDatasource
import com.techglock.health.app.remote.HraDatasource
import com.techglock.health.app.remote.MedicationDatasource
import com.techglock.health.app.remote.ParameterDatasource
import com.techglock.health.app.remote.SecurityDatasource
import com.techglock.health.app.remote.ShrDatasource
import com.techglock.health.app.remote.SudLifePolicyDatasource
import com.techglock.health.app.remote.ToolsCalculatorsDatasource
import com.techglock.health.app.remote.WaterTrackerDatasource
import com.techglock.health.app.remote.interceptor.DecryptInterceptor
import com.techglock.health.app.remote.interceptor.EncryptInterceptor
import com.techglock.health.app.repository.AktivoRepository
import com.techglock.health.app.repository.AktivoRepositoryImpl
import com.techglock.health.app.repository.BlogsRepository
import com.techglock.health.app.repository.BlogsRepositoryImpl
import com.techglock.health.app.repository.FitnessRepository
import com.techglock.health.app.repository.FitnessRepositoryImpl
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.HomeRepositoryImpl
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.HraRepositoryImpl
import com.techglock.health.app.repository.MedicationRepository
import com.techglock.health.app.repository.MedicationRepositoryImpl
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.ParameterRepositoryImpl
import com.techglock.health.app.repository.ShrRepositoryImpl
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.SudLifePolicyRepository
import com.techglock.health.app.repository.SudLifePolicyRepositoryImpl
import com.techglock.health.app.repository.ToolsCalculatorsRepository
import com.techglock.health.app.repository.ToolsCalculatorsRepositoryImpl
import com.techglock.health.app.repository.UserRepository
import com.techglock.health.app.repository.UserRepositoryImpl
import com.techglock.health.app.repository.WaterTrackerRepository
import com.techglock.health.app.repository.WaterTrackerRepositoryImpl
import com.techglock.health.app.water_tracker.common.WaterTrackerHelper
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DIModule {

    const val DEFAULT = "DEFAULT"
    const val DEFAULT_NEW = "DEFAULT_NEW"
    const val ENCRYPTED = "ENCRYPTED"
    const val SUD = "Sud"

/*    @Provides
    @Singleton
    fun provideSdkConfig(context: Context?): SdkConfig {
        //return ConfigurationUtils.setConfigData(context!!)
        ConfigurationUtils.getInstance()!!.clearData()
        return ConfigurationUtils.getInstance()!!.setConfigData(context!!)
    }*/

    @Singleton
    @Provides
    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory { }
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging==>$message")
        }.setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    @Singleton
    @Provides
    @Named(ENCRYPTED)
    fun provideEncryptedRetrofit(interceptor: Interceptor): Retrofit {
        return Retrofit.Builder().client(
            OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(EncryptInterceptor())
                .addInterceptor(interceptor)
                .certificatePinner(
                    CertificatePinner.Builder().add(
                        "com.sudlife.youmatter.app.uat",
                        "sha256/5FC7F54A491163C662E05558F0D643A269594A9CC5FB45CCA9EF3ED335B0755B"
                    ).build()
                )
                .addInterceptor(DecryptInterceptor())
                .build()
        ).baseUrl(Constants.strAPIUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }


    @Singleton
    @Provides
    @Named(DEFAULT)
    fun provideDefaultRetrofit(interceptor: Interceptor): Retrofit {
        return Retrofit.Builder().client(
            OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .certificatePinner(
                    CertificatePinner.Builder().add(
                        "com.sudlife.youmatter.app.uat",
                        "sha256/5FC7F54A491163C662E05558F0D643A269594A9CC5FB45CCA9EF3ED335B0755B"
                    ).build()
                )
                .addInterceptor(DecryptionInterceptor())
                .build()
        ).baseUrl(Constants.strAPIUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create()).build()
    }


    @Singleton
    @Provides
    @Named(DEFAULT_NEW)
    fun provideDefaultNewRetrofit(interceptor: Interceptor): Retrofit {
        return Retrofit.Builder().client(
            OkHttpClient.Builder()
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .certificatePinner(
                    CertificatePinner.Builder().add(
                        "com.sudlife.youmatter.app.uat",
                        "sha256/5FC7F54A491163C662E05558F0D643A269594A9CC5FB45CCA9EF3ED335B0755B"
                    ).build()
                )
                //.addInterceptor(DecryptionInterceptor())
                .build()
        ).baseUrl(Constants.strAPIUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named(SUD)
    fun provideSUDRetrofit(interceptor: Interceptor): Retrofit {
        return Retrofit.Builder().client(
            OkHttpClient.Builder().protocols(Arrays.asList(Protocol.HTTP_1_1))
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .certificatePinner(
                    CertificatePinner.Builder().add(
                        "com.sudlife.youmatter.app.uat",
                        "sha256/5FC7F54A491163C662E05558F0D643A269594A9CC5FB45CCA9EF3ED335B0755B"
                    ).build()
                )
                //.addInterceptor(DecryptionInterceptor())
                .build()
        ).baseUrl(Constants.strSudPolicyBaseURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }


    // Add similar methods for other Retrofit instances


    @Singleton
    @Provides
    @Named(ENCRYPTED)
    fun provideUserEncryptedApiService(@Named(ENCRYPTED) retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @Named(DEFAULT)
    fun provideDefaultApiService(@Named(DEFAULT) retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @Named(DEFAULT_NEW)
    fun provideDefaultNewApiService(@Named(DEFAULT_NEW) retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @Named(SUD)
    fun provideSudApiService(@Named(SUD) retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Add similar methods for other services

    @Singleton
    @Provides
    fun provideSecurityDatasource(
        @Named(DEFAULT) retrofit: ApiService, @Named(ENCRYPTED) encryptedRetrofit: ApiService
    ): SecurityDatasource {
        return SecurityDatasource(retrofit, encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideMedicationDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): MedicationDatasource {
        return MedicationDatasource(encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideFitnessDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): FitnessDatasource {
        return FitnessDatasource(encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideParameterDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): ParameterDatasource {
        return ParameterDatasource(encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideHraDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): HraDatasource {
        return HraDatasource(encryptedRetrofit)
    }


    @Singleton
    @Provides
    fun provideShrDatasource(
        @Named(DEFAULT) retrofit: ApiService, @Named(ENCRYPTED) encryptedRetrofit: ApiService
    ): ShrDatasource {
        return ShrDatasource(retrofit, encryptedRetrofit)
    }


    @Singleton
    @Provides
    fun provideToolsCalculatorsDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): ToolsCalculatorsDatasource {
        return ToolsCalculatorsDatasource(encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideWaterTrackerDatasource(@Named(ENCRYPTED) encryptedRetrofit: ApiService): WaterTrackerDatasource {
        return WaterTrackerDatasource(encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideSudLifePolicyDatasource(
        @Named(DEFAULT_NEW) defaultNewRetrofit: ApiService,
        @Named(ENCRYPTED) encryptedRetrofit: ApiService,
        @Named(SUD) sudRetrofit: ApiService
    ): SudLifePolicyDatasource {
        return SudLifePolicyDatasource(defaultNewRetrofit, encryptedRetrofit, sudRetrofit)
    }


    @Singleton
    @Provides
    fun provideBlogsDatasource(@Named(DEFAULT_NEW) defaultNewRetrofit: ApiService): BlogsDatasource {
        return BlogsDatasource(defaultNewRetrofit)
    }


    @Singleton
    @Provides
    fun provideHomeDatasource(
        @Named(DEFAULT) defaultNewRetrofit: ApiService,
        @Named(ENCRYPTED) encryptedRetrofit: ApiService
    ): HomeDatasource {
        return HomeDatasource(defaultNewRetrofit, encryptedRetrofit)
    }

    @Singleton
    @Provides
    fun provideAktivoDatasource(@Named(DEFAULT_NEW) defaultNewRetrofit: ApiService): AktivoDatasource {
        return AktivoDatasource(defaultNewRetrofit)
    }

    @Provides
    @Singleton
    fun providePreferenceUtils(context: Context?): PreferenceUtils {
        return PreferenceUtils(context!!.getSharedPreferences("Preferences", Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideStepCountHelper(context: Context?): StepCountHelper {
        return StepCountHelper(context!!)
    }

    @Provides
    @Singleton
    fun provideFitnessHelper(context: Context?): FitnessHelper {
        return FitnessHelper(context!!)
    }

    @Provides
    @Singleton
    fun provideWaterTrackerHelper(context: Context?): WaterTrackerHelper {
        return WaterTrackerHelper(context!!)
    }

    @Provides
    @Singleton
    fun provideMedicationTrackerHelper(context: Context?): MedicationTrackerHelper {
        return MedicationTrackerHelper(context!!)
    }

    @Provides
    @Singleton
    fun homeDataHandler(context: Context?): DataHandler {
        return DataHandler(context!!)
    }

    @Provides
    @Singleton
    fun toolCalculatorDataHandler(context: Context?): com.techglock.health.app.tools_calculators.common.DataHandler {
        return com.techglock.health.app.tools_calculators.common.DataHandler(context!!)
    }

    @Provides
    @Singleton
    fun recordsTrackerDataHandler(context: Context?): com.techglock.health.app.records_tracker.common.DataHandler {
        return com.techglock.health.app.records_tracker.common.DataHandler(context!!)
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context?): ArchAppDatabase {
        return ArchAppDatabase.buildDatabase(context!!)
    }

    @Provides
    @Singleton
    fun provideviVantUserDao(appDatabase: ArchAppDatabase): VivantUserDao {
        return appDatabase.vivantUserDao()
    }

    @Provides
    @Singleton
    fun provideviMedicationDao(appDatabase: ArchAppDatabase): MedicationDao {
        return appDatabase.medicationDao()
    }

    @Provides
    @Singleton
    fun provideviFitnessDao(appDatabase: ArchAppDatabase): FitnessDao {
        return appDatabase.fitnessDao()
    }

    @Provides
    @Singleton
    fun provideviHRADao(appDatabase: ArchAppDatabase): HRADao {
        return appDatabase.hraDao()
    }

    @Provides
    @Singleton
    fun provideviStoreRecordsDao(appDatabase: ArchAppDatabase): StoreRecordsDao {
        return appDatabase.shrDao()
    }

    @Provides
    @Singleton
    fun provideviDataSyncMasterDao(appDatabase: ArchAppDatabase): DataSyncMasterDao {
        return appDatabase.dataSyncMasterDao()
    }

    @Provides
    @Singleton
    fun provideviTrackParameterDao(appDatabase: ArchAppDatabase): TrackParameterDao {
        return appDatabase.trackParameterDao()
    }

    @Provides
    @Singleton
    fun provideviAppCacheMasterDao(appDatabase: ArchAppDatabase): AppCacheMasterDao {
        return appDatabase.appCacheMasterDao()
    }

    @Provides
    @Singleton
    fun getContext(application: Application?): Context? {
        return application?.applicationContext
    }

    @Provides
    @Singleton
    fun getMedicationRepository(
        medicationDatasource: MedicationDatasource,
        medicationDao: MedicationDao,
        dataSyncMasterDao: DataSyncMasterDao,
        context: Context?
    ): MedicationRepository {
        return MedicationRepositoryImpl(
            medicationDatasource, medicationDao, dataSyncMasterDao, context!!
        )
    }

    @Provides
    @Singleton
    fun getAktivoRepository(
        aktivoDatasource: AktivoDatasource, context: Context?
    ): AktivoRepository {
        return AktivoRepositoryImpl(aktivoDatasource, context!!)
    }

    @Provides
    @Singleton
    fun getBlogsRepository(datasource: BlogsDatasource, context: Context?): BlogsRepository {
        return BlogsRepositoryImpl(datasource, context!!)
    }

    @Provides
    @Singleton
    fun getFitnessRepository(
        dataSource: FitnessDatasource, fitnessDao: FitnessDao, context: Context?
    ): FitnessRepository {
        return FitnessRepositoryImpl(dataSource, fitnessDao, context!!)
    }

    @Provides
    @Singleton
    fun getHomeRepository(
        datasource: HomeDatasource,
        dataSyncDao: DataSyncMasterDao,
        homeDao: VivantUserDao,
        medicationDao: MedicationDao,
        shrDao: StoreRecordsDao,
        hraDao: HRADao,
        trackParamDao: TrackParameterDao,
        context: Context?
    ): HomeRepository {
        return HomeRepositoryImpl(
            datasource,
            dataSyncDao,
            homeDao,
            medicationDao,
            shrDao,
            hraDao,
            trackParamDao,
            context!!
        )
    }

    @Provides
    @Singleton
    fun getHraRepository(
        dataSource: HraDatasource,
        hraDao: HRADao,
        paramDao: TrackParameterDao,
        dataSyncMasterDao: DataSyncMasterDao,
        context: Context?
    ): HraRepository {
        return HraRepositoryImpl(dataSource, hraDao, paramDao, dataSyncMasterDao, context!!)
    }

    @Provides
    @Singleton
    fun getParameterRepository(
        dataSource: ParameterDatasource,
        paramDao: TrackParameterDao,
        dataSyncMasterDao: DataSyncMasterDao,
        context: Context?
    ): ParameterRepository {
        return ParameterRepositoryImpl(dataSource, paramDao, dataSyncMasterDao, context!!)
    }

    @Provides
    @Singleton
    fun getStoreRecordRepository(
        datasource: ShrDatasource,
        shrDao: StoreRecordsDao,
        userDao: VivantUserDao,
        dataSyncMasterDao: DataSyncMasterDao,
        context: Context?
    ): StoreRecordRepository {
        return ShrRepositoryImpl(datasource, shrDao, userDao, dataSyncMasterDao, context!!)
    }

    @Provides
    @Singleton
    fun getSudLifePolicyRepository(
        datasource: SudLifePolicyDatasource, context: Context?
    ): SudLifePolicyRepository {
        return SudLifePolicyRepositoryImpl(datasource, context!!)
    }

    @Provides
    @Singleton
    fun getToolsCalculatorsRepository(
        datasource: ToolsCalculatorsDatasource, context: Context?
    ): ToolsCalculatorsRepository {
        return ToolsCalculatorsRepositoryImpl(datasource, context!!)
    }

    @Provides
    @Singleton
    fun getUserRepository(
        datasource: SecurityDatasource, vudao: VivantUserDao, context: Context?
    ): UserRepository {
        return UserRepositoryImpl(datasource, vudao, context!!)
    }

    @Provides
    @Singleton
    fun getWaterTrackerRepository(
        datasource: WaterTrackerDatasource, context: Context?
    ): WaterTrackerRepository {
        return WaterTrackerRepositoryImpl(datasource, context!!)
    }

    @Provides
    @Singleton
    fun getMedicineTrackerViewModel(
        application: Application,
        preferenceUtils: PreferenceUtils,
        useCase: MedicationManagementUseCase,
        medicationTrackerHelper: MedicationTrackerHelper,
        context: Context?
    ): MedicineTrackerViewModel {
        return MedicineTrackerViewModel(
            application,
            preferenceUtils,
            useCase,
            medicationTrackerHelper,
            context
        )
    }

    @Provides
    @Singleton
    fun getBackgroundCallViewModel(
        application: Application,
        useCase: BackgroundCallUseCase,
        preferenceUtils: PreferenceUtils,
        context: Context?
    ): BackgroundCallViewModel {
        return BackgroundCallViewModel(
            application,
            useCase,
            preferenceUtils,
            context
        )
    }


}
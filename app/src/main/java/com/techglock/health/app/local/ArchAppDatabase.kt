package com.techglock.health.app.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.EncryptionUtility
import com.techglock.health.app.local.converter.Converters
import com.techglock.health.app.local.dao.AppCacheMasterDao
import com.techglock.health.app.local.dao.DataSyncMasterDao
import com.techglock.health.app.local.dao.FitnessDao
import com.techglock.health.app.local.dao.HRADao
import com.techglock.health.app.local.dao.MedicationDao
import com.techglock.health.app.local.dao.StoreRecordsDao
import com.techglock.health.app.local.dao.TrackParameterDao
import com.techglock.health.app.local.dao.VivantUserDao
import com.techglock.health.app.model.entity.AppCacheMaster
import com.techglock.health.app.model.entity.AppVersion
import com.techglock.health.app.model.entity.DataSyncMaster
import com.techglock.health.app.model.entity.DocumentType
import com.techglock.health.app.model.entity.FitnessEntity
import com.techglock.health.app.model.entity.HRALabDetails
import com.techglock.health.app.model.entity.HRAQuestions
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.HRAVitalDetails
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.entity.TrackParameters
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Users::class, AppVersion::class, HRAQuestions::class, HRAVitalDetails::class, HRALabDetails::class, HRASummary::class, HealthDocument::class, DocumentType::class, DataSyncMaster::class, RecordInSession::class, UserRelatives::class, MedicationEntity.Medication::class, FitnessEntity.StepGoalHistory::class, TrackParameterMaster.Parameter::class, TrackParameterMaster.TrackParameterRanges::class, TrackParameterMaster.History::class, TrackParameters::class, AppCacheMaster::class],
    version = 14,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ArchAppDatabase : RoomDatabase() {

    // DAO
    abstract fun vivantUserDao(): VivantUserDao
    abstract fun medicationDao(): MedicationDao
    abstract fun fitnessDao(): FitnessDao
    abstract fun hraDao(): HRADao
    abstract fun shrDao(): StoreRecordsDao
    abstract fun dataSyncMasterDao(): DataSyncMasterDao
    abstract fun trackParameterDao(): TrackParameterDao
    abstract fun appCacheMasterDao(): AppCacheMasterDao


    companion object {

        val DB_Name = Constants.MAIN_DATABASE_NAME

        fun buildDatabase(context: Context?): ArchAppDatabase {
            return Room.databaseBuilder(context!!, ArchAppDatabase::class.java, DB_Name)
                .fallbackToDestructiveMigration()
                //TODO : Below line is used for DB Security(Encryption and Decryption)
                //To Inspect Database , Comment below line
                .openHelperFactory(
                    SupportFactory(
                        SQLiteDatabase.getBytes(
                            String(
                                EncryptionUtility.IVkey, charset(EncryptionUtility.CHARSET)
                            ).toCharArray()
                        )
                    )
                ).build()
        }
    }
}
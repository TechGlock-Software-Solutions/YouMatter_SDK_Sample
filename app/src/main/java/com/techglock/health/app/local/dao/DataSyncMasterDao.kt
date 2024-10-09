package com.techglock.health.app.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techglock.health.app.model.entity.DataSyncMaster

@Dao
interface DataSyncMasterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApiSyncData(data: DataSyncMaster)

    @Query("SELECT * FROM DataSyncMaster WHERE apiName=:apiName AND personId=:personId")
    fun getLastSyncDate(apiName: String, personId: String): DataSyncMaster

    @Query("SELECT * FROM DataSyncMaster WHERE personId=:personId OR personId = '007'")
    fun getLastSyncDataList(personId: String): List<DataSyncMaster>

    @Update
    fun updateRecord(data: DataSyncMaster)

    @Query("DELETE FROM DataSyncMaster")
    fun deleteAllRecords()

}
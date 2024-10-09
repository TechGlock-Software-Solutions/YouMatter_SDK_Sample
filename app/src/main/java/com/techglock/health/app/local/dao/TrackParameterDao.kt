package com.techglock.health.app.local.dao

//import com.caressa.allizhealth.app.model.tempconst.Configuration
import androidx.room.*
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.entity.TrackParameters
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.parameter.*

@Dao
abstract class TrackParameterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insert(records: List<TrackParameterMaster.Parameter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insertParamRanges(records: List<TrackParameterMaster.TrackParameterRanges>)

    @Query("DELETE FROM TrackParameterMaster")
    abstract fun deleteAllRecords()

    @Query("DELETE FROM TrackParameterRanges")
    abstract fun deleteAllRangeData()

    @Query("DELETE FROM TrackParameterHistory")
    abstract fun deleteHistory()

    @Query("DELETE FROM TrackParameterHistory where personID !=:personId")
    abstract fun deleteHistoryWithOtherPersonId(personId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertHistory(records: List<TrackParameterMaster.History>): List<Long>

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT DISTINCT profileCode, profileName FROM TrackParameterMaster WHERE profileCode != 'OTHER' AND profileCode != '' ORDER by ProfileName ASC")
    abstract fun getSelectParameterList(): List<TrackParameterMaster.Parameter>

    @Query("SELECT * FROM TrackParameterHistory")
    abstract fun getTrackParameterHistoryAll(): List<TrackParameterMaster.History>

    @Query("SELECT * FROM TrackParameterMaster WHERE profileName != 'Other'")
    abstract fun getParameterData(): List<TrackParameterMaster.Parameter>

    @Query("SELECT * FROM TrackParameterMaster WHERE profileCode =:profileCode AND code NOT LIKE '%RATIO%'")
    abstract fun getParameterDataByProfileCode(profileCode: String): List<TrackParameterMaster.Parameter>

//    @Query("Select A.code as 'pCode', B.value as 'hVal'  From TrackParameterMaster A inner join TrackParameterHistory B On A.code = B.parameterCode")
//    abstract fun getParameterDashboardData():List<TrackParameterMaster.ParamDashboardData>

    @Query("select parameterCode, profileCode, max(recordDate) as MaxDate, value from TrackParameterHistory group by parameterCode")
    abstract fun getDashboardResponse(): List<DashboardData>

    //    @Query("SELECT C.code, C.profileCode, C.minAge, C.maxAge, C.minValue, C.maxValue,C.observation, C.rangeType, C.unit, D.value, D.MaxDate from TrackParameterRanges C INNER JOIN  (SELECT A.description, A.code as code, B.value, B.MaxDate from TrackParameterMaster A INNER JOIN (select parameterCode, profileCode, max(recordDate) as MaxDate, value from TrackParameterHistory group by parameterCode) B On A.code = B.parameterCode) D ON C.code = D.code")
    @Query("SELECT C.code, C.profileCode, C.minAge, C.maxAge, C.minValue, C.maxValue,C.observation, C.rangeType, C.unit, D.value, D.MaxDate from TrackParameterRanges C LEFT JOIN  (SELECT A.description, A.code as code, B.value, B.MaxDate from TrackParameterMaster A INNER JOIN (select parameterCode, profileCode, max(recordDate) as MaxDate, value from TrackParameterHistory group by parameterCode) B On A.code = B.parameterCode) D ON C.code = D.code GROUP by C.profileCode")
    abstract fun getDashboardObservationData(): List<DashboardObservationData>

    @Query("SELECT * from TrackParameterMaster where profileCode =:code and code not like '%RATIO%'")
    abstract fun getParameterListBaseOnCode(code: String): List<TrackParameterMaster.Parameter>

    @Query("SELECT * from TrackParameterHistory where parameterCode =:pCode AND personID =:personId ORDER by recordDate DESC LIMIT 3")
    abstract fun getParameterHisBaseOnCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT * from TrackParameterHistory where profileCode =:pCode AND personID =:personId ORDER by recordDate DESC")
    abstract fun getParameterHisBaseOnProfileCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT personID,parameterCode,description,profileCode,value,observation,recordDate,max(recordDateMillisec) as recordDateMillisec,textValue,ownerCode,sync,unit,createdBy,createdDate,modifiedBy FROM TrackParameterHistory WHERE personID =:personId  group by parameterCode HAVING ProfileCode =:pCode AND value != ''")
    abstract fun getLatestParametersBaseOnHisProfileCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT personID,parameterCode,description,profileCode,value,observation,recordDate,max(recordDateMillisec) as recordDateMillisec,textValue,ownerCode,sync,unit,createdBy,createdDate,modifiedBy FROM TrackParameterHistory WHERE personID =:personId  group by parameterCode HAVING ProfileCode =:pCode OR ProfileCode =:pCodeTwo")
    abstract fun getLatestParametersBaseOnHisProfileCodes(
        pCode: String,
        pCodeTwo: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("select * from TrackParameterHistory where personID =:personId and parameterCode=:paramCode and  (modifiedDate) in (select max(modifiedDate) from TrackParameterHistory group by parameterCode)")
    abstract fun getLatestParameterBaseOnHisParameterCode(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT * from TrackParameterHistory where parameterCode =:pCode AND personID =:personId  ORDER by recordDate DESC LIMIT :limit")
    abstract fun getParameterBasedOnProfileCodeAndDate(
        pCode: String,
        personId: String,
        limit: Int
    ): List<TrackParameterMaster.History>

    @Query("select * from TrackParameterRanges where  code = :pCode AND observation != \"NA\" AND (gender = :gender OR gender = 0) AND :age >= cast(minAge as int) AND :age <= cast(maxAge as int) ORDER BY cast(minValue as double)")
    abstract fun getParameterObservationBasedOnParameterCode(
        pCode: String,
        age: Int,
        gender: Int
    ): List<TrackParameterMaster.TrackParameterRanges>

    @Query("SELECT A.code as parameterCode, A.parameterType, A.description, A.profileCode, A.profileName, A.unit as parameterUnit,A.minPermissibleValue, A.maxPermissibleValue, B.textValue as parameterTextVal, B.value as parameterVal FROM TrackParameterMaster A LEFT JOIN TrackParameterHistory B ON A.profileCode=B.profileCode AND A.code=B.parameterCode AND B.personID = :personId AND A.profileCode = :profileCode AND B.recordDate= :recordDate  AND B.parameterCode NOT LIKE \"%RATIO%\" where A.profileCode = :profileCode AND A.code NOT LIKE '%RATIO%' ORDER BY A.profileCode DESC , A.code DESC")
    abstract fun getParameterDataByRecordDate(
        profileCode: String,
        recordDate: String,
        personId: String
    ): List<ParameterListModel.InputParameterModel>

    //    @Query("SELECT personID,parameterCode,description,profileCode,value,observation,recordDate,max(recordDateMillisec) as recordDateMillisec,textValue,ownerCode,sync,unit,createdBy,createdDate,modifiedBy FROM TrackParameterHistory WHERE personID =:personId  group by parameterCode HAVING ProfileCode =:pCode")
    @Query("select A.code as parameterCode, A.parameterType, A.description, A.profileCode, A.profileName, A.unit as parameterUnit,A.minPermissibleValue, A.maxPermissibleValue, B.textValue as parameterTextVal, B.value as parameterVal from TrackParameterMaster A  LEFT JOIN (select max(recordDateMillisec) as maxDate, value, profileCode, textValue, parameterCode, personID, recordDate, unit from TrackParameterHistory where personID= :personId group by parameterCode having profileCode = :profileCode) B ON A.code = B.parameterCode AND A.profileCode = B.profileCode where A.profileCode = :profileCode  AND A.code NOT LIKE '%RATIO%' ORDER BY A.profileCode DESC , A.code DESC")
    abstract fun getLatestDataOfParameter(
        profileCode: String,
        personId: String
    ): List<ParameterListModel.InputParameterModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertTrackParameter(parameter: TrackParameters)

    @Query("SELECT * FROM TrackParameters")
    abstract fun getTrackParametersList(): List<TrackParameters>

    @Query("DELETE FROM TrackParameters WHERE ProfileCode=:profileCode")
    abstract fun deletFromTrackParametersList(profileCode: String)

    @Query("DELETE FROM TrackParameters")
    abstract fun deletTrackParametersTable()

    @Query("SELECT * FROM UserRelativesTable WHERE relativeID =:personId")
    abstract fun getRelativeInformation(personId: String): List<UserRelatives>

    @Query("select B.parameterID,B.parameterCode, A.description, B.recordDate, B.recordDateMillisec, B.value,A.unit,B.observation,A.profileCode,A.profileName,B.personID,B.range,B.normalRange,B.displayRange,B.comments,B.textValue,B.ownerCode,B.createdBy,B.createdDate,B.modifiedBy,B.modifiedDate,B.sync from TrackParameterMaster A INNER JOIN TrackParameterHistory B ON A.profileCode = B.profileCode AND A.code = B.parameterCode WHERE B.personID = :personId AND B.parameterCode = :paramCode AND A.code NOT LIKE '%RATIO%' AND B.parameterCode NOT LIKE '%RATIO%' ORDER BY B.recordDateMillisec DESC LIMIT 5")
    abstract fun getParameterCodeHistory(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History>

    @Query("select * from TrackParameterMaster where code =:paramCode")
    abstract fun getParameterDetails(paramCode: String): List<TrackParameterMaster.Parameter>

    //Rohit
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT DISTINCT profileCode, profileName FROM TrackParameterMaster WHERE profileCode != 'OTHER' AND profileCode != '' ORDER by ProfileName ASC")
    abstract fun getAllProfileCodes(): List<TrackParameterMaster.Parameter>

    @Query("SELECT * FROM TrackParameterHistory WHERE personID =:personId ORDER by ProfileCode ASC")
    abstract fun getTrackParameterHistory(personId: String): List<TrackParameterMaster.History>

    @Query("SELECT * FROM TrackParameterHistory where profileCode =:profileCode AND recordDate LIKE :month AND recordDate LIKE :year AND value != 'NULL' AND value != 'null' AND personID =:personId ORDER BY recordDate DESC,parameterID")
    abstract fun listHistoryByProfileCodeAndMonthYear(
        personId: String,
        profileCode: String,
        month: String,
        year: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT * FROM TrackParameterHistory where profileCode =:profileCode AND recordDate LIKE :month AND recordDate LIKE :year AND textValue != 'NULL' AND textValue != 'null' AND personID =:personId ORDER BY recordDate DESC,parameterID")
    abstract fun listHistoryByProfileCodeAndMonthYearForUrineProfile(
        personId: String,
        profileCode: String,
        month: String,
        year: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT personID,parameterCode,description,profileCode,value,observation,recordDate,max(recordDateMillisec) as recordDateMillisec,textValue,ownerCode,sync,unit,createdBy,createdDate,modifiedBy FROM TrackParameterHistory WHERE personID =:personId  group by parameterCode HAVING ProfileCode =:profileCode  AND parameterCode =:parameterCode")
    abstract fun listHistoryWithLatestRecord(
        personId: String,
        profileCode: String,
        parameterCode: String
    ): List<TrackParameterMaster.History>

    @Query("SELECT personID,parameterCode,description,profileCode,value,observation,recordDate,max(recordDateMillisec) as recordDateMillisec,textValue,ownerCode,sync,unit,createdBy,createdDate,modifiedBy FROM TrackParameterHistory WHERE personID =:personId  group by parameterCode HAVING ProfileCode =:profileCode")
    abstract fun listHistoryWithLatestRecord(
        personId: String,
        profileCode: String
    ): List<TrackParameterMaster.History>

    fun save(data: ParameterListModel.Response) {
        try {
            var parmItem: TrackParameterMaster.Parameter
            var paramRangeItem: TrackParameterMaster.TrackParameterRanges
            val paramRangeList: ArrayList<TrackParameterMaster.TrackParameterRanges> = arrayListOf()
            val paramList: ArrayList<TrackParameterMaster.Parameter> = arrayListOf()
            try {
                for (item in data.parameters) {
                    parmItem = TrackParameterMaster.Parameter(
                        iD = item.iD,
                        code = item.code,
                        description = item.description,
                        hasUnit = item.hasUnit,
                        mandatory = item.isMandatory,
                        minPermissibleValue = item.minPermissibleValue,
                        maxPermissibleValue = item.maxPermissibleValue,
                        comments = "",
                        profileCode = item.profileCode,
                        profileName = item.profileName,
                        unit = item.unit,
                        parameterType = item.parameterType,
                        recordExist = item.isRecordExist
                    )
//            Utilities.printLog("Param=> "+parmItem)
                    paramList.add(parmItem)
                    if (item.labParameterRanges != null) {
                        for (rangeItem in item.labParameterRanges) {
                            paramRangeItem = TrackParameterMaster.TrackParameterRanges(
                                paramId = item.iD,
                                profileCode = item.profileCode,
                                code = item.code,
                                displayRange = rangeItem.displayRange,
                                gender = rangeItem.gender,
                                isRecordExist = rangeItem.isRecordExist,
                                maxAge = rangeItem.maxAge,
                                minAge = rangeItem.minAge,
                                minValue = rangeItem.minValue,
                                maxValue = rangeItem.maxValue,
                                observation = rangeItem.observation,
                                rangeType = rangeItem.rangeType,
                                unit = rangeItem.unit
                            )
                            paramRangeList.add(paramRangeItem)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            insert(paramList)
            Utilities.printLog("Range Size=> ${paramRangeList.size}")
            insertParamRanges(paramRangeList)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveBMIHistory(data: BMIHistoryModel.Response) {
        try {
            var bmiItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> = arrayListOf()
            for (item in data.bMIHistory) {
                if (!Utilities.isNullOrEmptyOrZero(item.height.toString())) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.height,
                        parameterCode = "HEIGHT",
                        description = Utilities.getLanguageDescription(
                            "HEIGHT",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!Utilities.isNullOrEmptyOrZero(item.weight.toString())) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.weight,
                        parameterCode = "WEIGHT",
                        description = Utilities.getLanguageDescription(
                            "WEIGHT",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "Kg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!Utilities.isNullOrEmptyOrZero(item.value)) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.value.toDouble(),
                        parameterCode = "BMI",
                        description = "BMI",
                        observation = item.observation,
                        recordDateMillisec = item.recordDateMillisec,
                        recordDate = item.recordDate,
                        unit = "kg/m2",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveWHRHistory(data: WHRHistoryModel.Response) {
        try {
            var bmiItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> =
                arrayListOf<TrackParameterMaster.History>()
            for (item in data.wHRHistory) {
                if (item.waist != 0.0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.waist,
                        parameterCode = "WAIST",
                        description = Utilities.getLanguageDescription(
                            "WAIST",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (item.hip != 0.0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.hip,
                        parameterCode = "HIP",
                        description = Utilities.getLanguageDescription(
                            "HIP",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!item.value.equals("", true)) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.value.toDouble(),
                        parameterCode = "WHR",
                        description = "WHR",
                        observation = item.observation,
                        recordDateMillisec = item.recordDateMillisec,
                        recordDate = item.recordDate,
                        unit = "",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveBloodPressureHistory(data: BloodPressureHistoryModel.Response) {
        try {
            var bmiItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> =
                arrayListOf<TrackParameterMaster.History>()
            for (item in data.bloodPressureHistory) {
                if (item.systolic != 0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.systolic.toDouble(),
                        parameterCode = "BP_SYS",
                        description = Utilities.getLanguageDescription(
                            "Systolic",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "mmHg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (item.diastolic != 0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.diastolic.toDouble(),
                        parameterCode = "BP_DIA",
                        description = Utilities.getLanguageDescription(
                            "Diastolic",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "mmHg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (item.pulse != 0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.pulse.toDouble(),
                        parameterCode = "BP_PULSE",
                        description = Utilities.getLanguageDescription(
                            "Pulse",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "bpm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun saveBMIHistoryVital(data: ArrayList<VitalsHistoryModel.VitalsHistory>) {
        try {
            var bmiItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> = arrayListOf()
            for (item in data) {
                if (!Utilities.isNullOrEmptyOrZero(item.height.toString())) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.height,
                        parameterCode = "HEIGHT",
                        description = Utilities.getLanguageDescription(
                            "HEIGHT",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!Utilities.isNullOrEmptyOrZero(item.weight.toString())) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.weight,
                        parameterCode = "WEIGHT",
                        description = Utilities.getLanguageDescription(
                            "WEIGHT",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "Kg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!Utilities.isNullOrEmptyOrZero(item.value)) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BMI",
                        value = item.value.toDouble(),
                        parameterCode = "BMI",
                        description = "BMI",
                        observation = item.observation,
                        recordDateMillisec = item.recordDateMillisec,
                        recordDate = item.recordDate,
                        unit = "kg/m2",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveWHRHistoryVital(data: ArrayList<VitalsHistoryModel.VitalsHistory>) {
        try {
            var bmiItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> =
                arrayListOf<TrackParameterMaster.History>()
            for (item in data) {
                if (item.waist != 0.0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.waist,
                        parameterCode = "WAIST",
                        description = Utilities.getLanguageDescription(
                            "WAIST",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (item.hip != 0.0) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.hip,
                        parameterCode = "HIP",
                        description = Utilities.getLanguageDescription(
                            "HIP",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "cm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
                if (!item.value.equals("", true)) {
                    bmiItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "WHR",
                        value = item.value.toDouble(),
                        parameterCode = "WHR",
                        description = "WHR",
                        observation = item.observation,
                        recordDateMillisec = item.recordDateMillisec,
                        recordDate = item.recordDate,
                        unit = "",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bmiItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveBloodPressureHistoryVital(data: ArrayList<VitalsHistoryModel.VitalsHistory>) {
        try {
            var bpItem: TrackParameterMaster.History
            val listHistory: ArrayList<TrackParameterMaster.History> =
                arrayListOf<TrackParameterMaster.History>()
            for (item in data) {
                if (item.systolic != 0) {
                    bpItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.systolic.toDouble(),
                        parameterCode = "BP_SYS",
                        description = Utilities.getLanguageDescription(
                            "Systolic",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "mmHg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bpItem)
                }
                if (item.diastolic != 0) {
                    bpItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.diastolic.toDouble(),
                        parameterCode = "BP_DIA",
                        description = Utilities.getLanguageDescription(
                            "Diastolic",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "mmHg",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bpItem)
                }
                if (item.pulse != 0) {
                    bpItem = TrackParameterMaster.History(
                        personID = item.personID,
                        profileCode = "BLOODPRESSURE",
                        value = item.pulse.toDouble(),
                        parameterCode = "BP_PULSE",
                        description = Utilities.getLanguageDescription(
                            "Pulse",
                            Configuration.LanguageCode
                        ),
                        recordDate = item.recordDate,
                        recordDateMillisec = item.recordDateMillisec,
                        unit = "bpm",
                        sync = false,
                        createdDate = item.createdDate,
                        modifiedDate = item.modifiedDate
                    )
                    listHistory.add(bpItem)
                }
            }
            insertHistory(listHistory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


package com.techglock.health.app.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.techglock.health.app.model.entity.AppCacheMaster
import com.techglock.health.app.model.entity.AppVersion
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users

@Dao
interface VivantUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Users)

    @Query("SELECT * FROM Users")
    fun getUsersData(): Users

    @Query("SELECT * FROM Users ")
    fun getUser(): Users

    @Query("UPDATE  Users SET PhoneNumber=:phone")
    fun updateUserMobileNumber(phone: String)

    /*    @Query("UPDATE Users SET IsOtpAuthenticated=:isOtpAuthenticated")
        fun updateOtpAuthentication(isOtpAuthenticated: Boolean)*/

    @Query("UPDATE  Users SET firstName=:name, dateOfBirth=:dateOfBirth,age=:age WHERE personId =:personId")
    fun updateName(name: String, dateOfBirth: String, age: Int, personId: Int)

    @Query("UPDATE  UserRelativesTable SET firstName=:name, dateOfBirth=:dateOfBirth,age=:age WHERE relativeID =:personId")
    fun updateUserInUserRelativesDetails(
        name: String,
        dateOfBirth: String,
        age: Int,
        personId: String
    )

    @Query("UPDATE  UserRelativesTable SET firstName=:name,emailAddress=:email,contactNo=:number,dateOfBirth=:dob WHERE relativeID =:relativeID")
    fun updateUserRelativesDetails(
        relativeID: String,
        name: String,
        email: String,
        number: String,
        dob: String
    )

    @Query("UPDATE  Users SET name=:name , path=:path")
    fun updateUserProfileImgPath(name: String, path: String)

    @Query("DELETE FROM Users")
    fun deleteAllRecords()

    // *****************************  RelativesTable  *****************************
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRelative(userRelatives: UserRelatives)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserRelative(userRelatives: List<UserRelatives>)

    @Query("SELECT * FROM UserRelativesTable")
    fun geAllRelativeList(): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelationshipCode != 'SELF'")
    fun getUserRelatives(): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelationshipCode =:relationShipCode")
    fun getUserRelativeSpecific(relationShipCode: String): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelativeID =:relativeId")
    fun getUserRelativeForRelativeId(relativeId: String): List<UserRelatives>

    @Query("SELECT * FROM UserRelativesTable WHERE RelativeID =:relativeId")
    fun getUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives

    @Query("SELECT Relationship FROM UserRelativesTable WHERE RelativeID=:relativeId")
    fun getRelationShip(relativeId: String): String

    @Query("DELETE FROM UserRelativesTable WHERE RelativeID=:relativeId")
    fun deleteUserRelativeById(relativeId: String)

    @Query("DELETE FROM UserRelativesTable")
    fun deleteUserRelativesTable()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppCacheData(cache: AppCacheMaster)

    @Query("SELECT * FROM AppCacheMaster WHERE mapKey=:key")
    fun getAppCache(key: String): List<AppCacheMaster>
    //*****************************  RelativesTable  *****************************

    //**************************  AppUpdateDetailsTable  **************************

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppVersionDetails(appVersion: AppVersion)

    @Query("SELECT * FROM AppUpdateDetailsTable")
    fun getAppVersionDetails(): AppVersion

    //**************************  AppUpdateDetailsTable  **************************
}
package com.techglock.health.app.local.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy

abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insert(users: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun insert(user: T)
}
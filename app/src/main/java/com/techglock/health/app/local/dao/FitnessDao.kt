package com.techglock.health.app.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.techglock.health.app.model.entity.FitnessEntity
import java.util.Date


@Dao
abstract class FitnessDao : BaseDao<FitnessEntity.StepGoalHistory>() {

    @Query("SELECT * FROM StepGoalHistory")
    abstract fun getUserStepsData(): List<FitnessEntity.StepGoalHistory>

    /**
     * Each time we save an user, we update its 'lastRefreshed' field
     * This allows us to know when we have to refresh its data
     */

    fun save(steps: FitnessEntity.StepGoalHistory) {
        insert(steps.apply { lastRefreshed = Date() })
    }

    fun save(steps: List<FitnessEntity.StepGoalHistory>) {
        insert(steps.apply { forEach { it.lastRefreshed = Date() } })
    }

    @Query("DELETE FROM StepGoalHistory")
    abstract fun deleteStepGoalHistoryTable()
}
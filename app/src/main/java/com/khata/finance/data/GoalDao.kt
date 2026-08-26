package com.khata.finance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM savings_goals ORDER BY id DESC")
    fun getAll(): Flow<List<SavingsGoal>>

    @Insert
    suspend fun insert(goal: SavingsGoal): Long

    @Update
    suspend fun update(goal: SavingsGoal)

    @Delete
    suspend fun delete(goal: SavingsGoal)
}

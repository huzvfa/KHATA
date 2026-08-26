package com.khata.finance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAll(): Flow<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'INCOME' AND dateMillis BETWEEN :start AND :end")
    fun getTotalIncome(start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'EXPENSE' AND dateMillis BETWEEN :start AND :end")
    fun getTotalExpense(start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(CASE WHEN type='INCOME' THEN amount ELSE -amount END),0) FROM transactions")
    fun getNetBalance(): Flow<Double>
}

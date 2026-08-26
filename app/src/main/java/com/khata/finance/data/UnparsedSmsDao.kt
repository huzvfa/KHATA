package com.khata.finance.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UnparsedSmsDao {
    @Query("SELECT * FROM unparsed_sms WHERE resolved = 0 ORDER BY receivedMillis DESC")
    fun getUnresolved(): Flow<List<UnparsedSms>>

    @Insert
    suspend fun insert(item: UnparsedSms): Long

    @Update
    suspend fun update(item: UnparsedSms)

    @Delete
    suspend fun delete(item: UnparsedSms)
}

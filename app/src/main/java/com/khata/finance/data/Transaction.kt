package com.khata.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TxnType { INCOME, EXPENSE }
enum class TxnSource { MANUAL, SMS }

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TxnType,
    val categoryId: Long?,
    val note: String,
    val merchant: String? = null,
    val dateMillis: Long,
    val source: TxnSource = TxnSource.MANUAL,
    val rawSms: String? = null
)

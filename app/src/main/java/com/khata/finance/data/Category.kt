package com.khata.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: TxnType,
    val icon: String = "💰",
    val colorHex: String = "#4CAF50",
    val monthlyBudget: Double? = null
)

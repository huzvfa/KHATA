package com.khata.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Safety net: any SMS that looks like it's from the bank but couldn't be
// confidently parsed lands here instead of silently vanishing.
@Entity(tableName = "unparsed_sms")
data class UnparsedSms(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val body: String,
    val receivedMillis: Long,
    val resolved: Boolean = false
)

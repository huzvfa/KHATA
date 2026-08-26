package com.khata.finance.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTxnType(value: TxnType): String = value.name
    @TypeConverter
    fun toTxnType(value: String): TxnType = TxnType.valueOf(value)

    @TypeConverter
    fun fromTxnSource(value: TxnSource): String = value.name
    @TypeConverter
    fun toTxnSource(value: String): TxnSource = TxnSource.valueOf(value)
}

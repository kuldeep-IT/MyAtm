package com.kuldeep.atm1.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class Transactions(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val totalAmt: Int?,
    val twoThsd: Int?,
    val fiveHndrd: Int?,
    val twoHndrd: Int?,
    val oneHndrd: Int?
    ) : Serializable

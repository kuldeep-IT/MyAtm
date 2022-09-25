package com.kuldeep.atm1.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kuldeep.atm1.data.Transactions


@Dao
interface AtmDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun withdrawAmt(transactions: Transactions): Long

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): LiveData<List<Transactions>>

}
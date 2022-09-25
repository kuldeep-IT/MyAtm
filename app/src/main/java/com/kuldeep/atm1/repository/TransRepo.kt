package com.kuldeep.atm1.repository

import androidx.lifecycle.LiveData
import com.kuldeep.atm1.data.Transactions
import com.kuldeep.atm1.db.TransactionDB

class TransRepo(val db: TransactionDB) {

    fun withdrawAmt(transactions: Transactions) = db.getAllTransactions().withdrawAmt(transactions)

    fun getAllTransactions(): LiveData<List<Transactions>> = db.getAllTransactions().getAllTransactions()

}
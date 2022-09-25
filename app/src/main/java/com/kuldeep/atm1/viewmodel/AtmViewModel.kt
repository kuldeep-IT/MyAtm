package com.kuldeep.atm1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuldeep.atm1.data.Transactions
import com.kuldeep.atm1.repository.TransRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AtmViewModel(
    val transRepo: TransRepo
) : ViewModel() {

    val allTransactions : LiveData<List<Transactions>>

    init {
        allTransactions = transRepo.getAllTransactions()
    }

    fun withdrawAmt(transactions: Transactions) =
        CoroutineScope(Dispatchers.IO).launch { transRepo.withdrawAmt(transactions) }

}
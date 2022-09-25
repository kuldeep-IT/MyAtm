package com.kuldeep.atm1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kuldeep.atm1.repository.TransRepo

class AtmVMProvider(val transRepo: TransRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AtmViewModel(transRepo) as T
    }
}
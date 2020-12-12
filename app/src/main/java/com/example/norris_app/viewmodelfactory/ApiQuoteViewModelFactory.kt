package com.example.norris_app.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.norris_app.viewmodel.ApiQuoteViewModel

class ApiQuoteViewModelFactory : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApiQuoteViewModel::class.java)) {
            return ApiQuoteViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
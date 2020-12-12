package com.example.norris_app.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.norris_app.database.UserDao
import com.example.norris_app.viewmodel.UserViewModel

class UserViewModelFactory(private val dataBase: UserDao, private val application: Application) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(dataBase, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
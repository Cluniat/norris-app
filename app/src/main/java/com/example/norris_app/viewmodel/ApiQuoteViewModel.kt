package com.example.norris_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.norris_app.service.NorrisApi
import com.example.norris_app.utils.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ApiQuoteViewModel : ViewModel() {

    private val _response = MutableLiveData<ApiResponse>()

    val response: LiveData<ApiResponse>
        get() = _response

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val apiResponse = ApiResponse("", false, false, false)

    init {
        Log.i("ApiQuoteViewModel", "created")
        apiResponse.response = "Tap the button below to generate a random Chuck Norris fact"
        _response.value = apiResponse
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ApiQuoteViewModel", "destroyed")
        viewModelJob.cancel()
    }

    /**
     * Generate random quote calling API
     */
    fun getRandomQuote() {
        uiScope.launch {
            val getPropertiesDeferred = NorrisApi.retrofitService.getProperties()
            try {
                apiResponse.isLoading = true
                _response.value = apiResponse
                val result = getPropertiesDeferred.await()
                apiResponse.response = result.value
                apiResponse.isLoading = false
                apiResponse.isSuccess = true
                _response.value = apiResponse
            } catch (e: Exception) {
                apiResponse.isError = true
                apiResponse.response = "Failure: ${e.message}"
                _response.value = apiResponse
            }
        }
    }
}
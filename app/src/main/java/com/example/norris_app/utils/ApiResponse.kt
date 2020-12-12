package com.example.norris_app.utils


class ApiResponse(
    var response: String,
    var isSuccess: Boolean,
    var isError: Boolean,
    var isLoading: Boolean
)
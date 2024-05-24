package com.example.ktor_server

data class StartAppRequest(
    val ip: String,
    val username: String,
    val password: String,
    val appName: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)

data class AppInfo(
    val name: String,
    val packageFamilyName: String
)

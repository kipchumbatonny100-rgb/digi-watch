package com.example.secureus.data

import com.google.gson.annotations.SerializedName

data class UserReportRequest(
    val userId: Int,
    val address: String,
    val isSafe: Boolean,
    val incidentType: String? = null
)

data class NetworkResponse(
    val message: String,
    val status: String,
    val token: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: Int,
    val name: String,
    val role: String = "user"
)

data class UserStatusResponse(
    val id: Int,
    val name: String,
    val address: String,
    @SerializedName("isSafe")
    val isSafe: Boolean
)

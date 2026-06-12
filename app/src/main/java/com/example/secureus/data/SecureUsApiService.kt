package com.example.secureus.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface SecureUsApiService {
    @POST("api/register")
    suspend fun register(@Body request: Map<String, String>): NetworkResponse

    @POST("api/login")
    suspend fun login(@Body request: Map<String, String>): NetworkResponse

    @POST("api/settings/update")
    suspend fun updateSettings(@Body request: Map<String, String>): NetworkResponse

    @POST("api/settings/change-password")
    suspend fun changePassword(@Body request: Map<String, String>): NetworkResponse

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: Map<String, String>): NetworkResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: Map<String, String>): NetworkResponse

    @GET("api/user/{id}")
    suspend fun getUserStatus(@Path("id") userId: Int): UserStatusResponse

    @POST("api/report")
    suspend fun submitReport(@Body request: UserReportRequest): NetworkResponse

    @GET("api/reports")
    suspend fun getAllReports(): List<UserReportRequest>

    companion object {
        // Change this to your computer's IP (e.g. 10.0.0.179) if testing on a physical device.
        // If testing on an Android Emulator, use 10.0.2.2 (points to your host computer).
        private const val BASE_URL = "http://10.0.0.179:3000/"

        fun create(): SecureUsApiService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SecureUsApiService::class.java)
        }
    }
}

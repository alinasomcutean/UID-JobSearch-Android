package com.example.jobssearch.rest_api

import com.example.jobssearch.model.api.GetJobsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface JobsAPI {

    @GET("positions.json")
    @Headers("Content-Type: application/json")
    fun getJobs(@Query("description") category: String): Call<ArrayList<GetJobsResponse>>

    companion object {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        fun create(): JobsAPI {
            val retrofitInstance = Retrofit.Builder()
                .baseUrl("https://jobs.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()

            return retrofitInstance.create(JobsAPI::class.java)
        }
    }
}
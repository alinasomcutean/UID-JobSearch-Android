package com.example.jobssearch.model.api

data class GetJobsResponse (
    val type: String,
    val company: String,
    val location: String,
    val title: String,
    val company_logo: String
)
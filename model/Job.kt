package com.example.jobssearch.model

import java.io.Serializable

class Job (
    val type: String,
    val company: String,
    val location: String,
    val title: String,
    val company_logo: String,
    var applied: Boolean = false,
    var removeApplication: Boolean = false
): Serializable
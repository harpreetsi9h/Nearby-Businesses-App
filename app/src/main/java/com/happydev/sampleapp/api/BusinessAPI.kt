package com.happydev.sampleapp.api

import com.happydev.sampleapp.models.BusinessesModel
import com.happydev.sampleapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface BusinessAPI {

    @Headers("Authorization: Bearer "+Constants.API_KEY)
    @GET(Constants.SEARCH_URL)
    suspend fun searchBusinesses(
        @Query("term")
        searchTerm : String,
        @Query("location")
        location : String,
        @Query("offset")
        offset : String
    ): Response<BusinessesModel>
}
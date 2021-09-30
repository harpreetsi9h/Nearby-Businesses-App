package com.happydev.sampleapp.repository

import com.happydev.sampleapp.api.RetrofitInstance

class BusinessRepository {
    suspend fun searchBusiness(searchTerm: String, location: String, offset: String) =
        RetrofitInstance.api.searchBusinesses(searchTerm, location, offset)
}
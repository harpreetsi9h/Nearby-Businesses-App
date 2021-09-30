package com.happydev.sampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.happydev.sampleapp.repository.BusinessRepository

class BusinessViewModelProviderFactory(
    private val app: App
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BusinessViewModel(app) as T
    }

}
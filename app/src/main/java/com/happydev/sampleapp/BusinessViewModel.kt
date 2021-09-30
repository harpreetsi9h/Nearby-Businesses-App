package com.happydev.sampleapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.happydev.sampleapp.models.BusinessesModel
import com.happydev.sampleapp.repository.BusinessRepository
import com.happydev.sampleapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class BusinessViewModel(
    private val app: App
) : AndroidViewModel(app) {

    private val repository = BusinessRepository()
    val businesses : MutableLiveData<Resource<BusinessesModel>> = MutableLiveData()
    private var businessesResponse : BusinessesModel? = null

    fun searchBusinesses(term: String, location: String, isRefresh : Boolean) = viewModelScope.launch {
        if(isRefresh)
            businesses.value?.data?.businesses?.clear()
        safeSearchBusiness(term, location)
    }

    private fun handleSearchBusinessResponse(response: Response<BusinessesModel>) : Resource<BusinessesModel> {
        if(response.isSuccessful) {
            response.body()?.let { reslutResponse ->
                if(businessesResponse==null) {
                    businessesResponse = reslutResponse
                } else {
                    val oldValue = businessesResponse?.businesses
                    val newValue = reslutResponse.businesses
                    newValue?.let { oldValue?.addAll(newValue) }
                }
                return Resource.Success(businessesResponse ?: reslutResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeSearchBusiness(term: String, location: String) {
        var offset = 0
        businesses.value?.data?.businesses?.size?.let {
            offset = it
        }
        businesses.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = repository.searchBusiness(term, location, offset.toString())
                businesses.postValue(handleSearchBusinessResponse(response))
            } else {
                businesses.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> businesses.postValue(Resource.Error("Network Failure!"))
                else -> businesses.postValue(Resource.Error("Something Went Wrong!"))
            }
        }
    }

    private fun hasInternetConnection() : Boolean{
        val connectivityManager = app.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
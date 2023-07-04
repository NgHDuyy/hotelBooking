package com.example.myBookingHotel.viewmodel.liveData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myBookingHotel.model.hotel.Hotel

class MainViewModel : ViewModel() {

    companion object{
        const val ON_RESTART_APP = "ON_RESTART_APP"
        const val ON_UPDATE_FAVORITE = "ON_UPDATE_FAVORITE"
    }

    private val onAddToFavorite = MutableLiveData<Hotel>()
    val getEventAddToFavorite: LiveData<Hotel> get() = onAddToFavorite
    fun setEventAddToFavorite(value: Hotel){
        onAddToFavorite.value = value
    }

    private val onRemoveFavorite = MutableLiveData<Hotel>()
    val getEventRemoveFavorite: LiveData<Hotel> get() = onRemoveFavorite
    fun setEventRemoveFavorite(value: Hotel){
        onRemoveFavorite.value = value
    }

    private val onRemoveFavoriteToHome = MutableLiveData<Hotel>()
    val getEventRemoveFavoriteToHome: LiveData<Hotel> get() = onRemoveFavoriteToHome
    fun setEventRemoveFavoriteToHome(value: Hotel){
        onRemoveFavoriteToHome.value = value
    }

    private val onRestartItem = MutableLiveData<String>()
    val eventOnRestart: LiveData<String> get() = onRestartItem
    fun setEventOnRestart(event: String) {
        onRestartItem.value = event
    }
}
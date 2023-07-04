package com.example.myBookingHotel.model.hotel

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("province")
    var province: String?,
    @SerializedName("district")
    var district: String?,
    @SerializedName("address")
    var address: String?
)

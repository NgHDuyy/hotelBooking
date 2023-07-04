package com.example.myBookingHotel.model.hotel

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("id")
    var idRoom: String?,
    @SerializedName("room_number")
    var roomNumber: String?,
    @SerializedName("room_name")
    var roomName: String?,
    @SerializedName("number_bed")
    var numberBed: Int?,
    @SerializedName("maximum_quantity")
    var maximumPeople: Int?,
    @SerializedName("price")
    var price: Long?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("listURL")
    var listURL: List<String>?,
    @SerializedName("status")
    var isEmpty: Boolean
)
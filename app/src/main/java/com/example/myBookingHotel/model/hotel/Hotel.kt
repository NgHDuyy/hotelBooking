package com.example.myBookingHotel.model.hotel

import com.google.gson.annotations.SerializedName

data class Hotel(
    @SerializedName("id")
    var hotelId: String,
    @SerializedName("name")
    var hotelName: String?,
    @SerializedName("location")
    var location: Location?,
    @SerializedName("absPrice")
    var absPrice: Long?,
    @SerializedName("images")
    var image: List<String>?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("start")
    var star: Int?,
    @SerializedName("vote")
    var vote: Double?,
    @SerializedName("vote_total")
    var voteTotal: Int?,
    @SerializedName("rooms")
    var listRoom: List<Room>?,
    var isFavorite: Boolean = false
)
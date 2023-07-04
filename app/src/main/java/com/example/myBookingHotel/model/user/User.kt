package com.example.myBookingHotel.model.user

import com.example.myBookingHotel.model.user.Card
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var userId: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("password")
    var password: String?,
    @SerializedName("phone")
    var phoneNumber: String?,
    @SerializedName("cards")
    var cards: List<Card>?
)
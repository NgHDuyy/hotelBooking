package com.example.myBookingHotel.model.user

import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("card_id")
    var cardId: String?,
    @SerializedName("id_customer")
    var customerId: String?,
    @SerializedName("card_number")
    var cardNumber: String?,
    @SerializedName("name_customer")
    var name: String?,
    @SerializedName("pin_code")
    var pinCode: String?,
    @SerializedName("bank_name")
    var bankName: String?
)
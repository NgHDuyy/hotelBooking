package com.example.myBookingHotel.model.booking

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Bill(
    @SerializedName("id")
    var billId: String?,
    @SerializedName("customer_id")
    var userId: String?,
    @SerializedName("customer_name")
    var userName: String?,
    @SerializedName("customer_phone")
    var userPhone: String?,
    @SerializedName("hotel_id")
    var hotelId: String?,
    @SerializedName("room_id")
    var roomId: String?,
    @SerializedName("room_name")
    var roomName: String?,
    @SerializedName("room_number")
    var roomNumber: String?,
    @SerializedName("start_date")
    var checkIn: Date?,
    @SerializedName("end_date")
    var checkOut: Date?,
    @SerializedName("price")
    var totalPrice: Double?
)
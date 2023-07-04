package com.example.myBookingHotel.view.activity.booking

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ActivityBookingDetailBinding
import com.example.myBookingHotel.model.booking.Bill
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.model.hotel.Room
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.text.SimpleDateFormat

class BookingDetailActivity : BaseActivity() {
    private var binding: ActivityBookingDetailBinding? = null

    private var bill: Bill? = null
    private var hotel: Hotel? = null
    private var room: Room? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        intent?.extras.let {
            val billJson = it?.getString("bill")
            bill = try {
                Gson().fromJson(billJson, Bill::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
        }
        loadData()
        onClick()
    }

    private fun initUi() {
        binding?.apply {
            val imageBytes: ByteArray = Base64.decode(room?.listURL!![0], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Glide.with(this@BookingDetailActivity).load(bitmap)
                .into(imgRoom)

            tvRoomName.text = room?.roomName
            tvRoomNumber.text = room?.roomNumber
            val strBedType =
                if (room?.numberBed == 1) getString(R.string.single_bed)
                else getString(
                    R.string.double_bed
                )
            tvTotalBed.text = "${getString(R.string.txt_type_bed)}: $strBedType"
            tvQuantityPeople.text =
                "${getString(R.string.txt_quantity_people)}: ${room?.maximumPeople}"
            tvRoomPrice.text = "${room?.price} ${preferenceHelper.moneyType()}"
            tvRoomDes.text = room?.description
            tvRoomDes.visibility = View.GONE
            edtUserName.hint = preferenceHelper.getInfoUser(value = 1)
            edtEmail.hint = preferenceHelper.getInfoUser(value = 2)
            edtPhone.hint = preferenceHelper.getInfoUser(value = 3)

            tvTimeCheckIn.text = dateFormat.format(bill?.checkIn!!)
            tvTimeCheckOut.text = dateFormat.format(bill?.checkOut!!)
            tvBook.text = getString(R.string.txt_delete_book)
        }
    }

    private var dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private fun loadData() {
        APIHelper().getHotelById(bill?.hotelId!!, onPre = {
            showLoading(true)
        }, onSuccess = {
            hotel = try {
                Gson().fromJson(it, Hotel::class.java)
            } catch (e: JsonSyntaxException) {
                null
            }
            for (itemRoom in hotel?.listRoom!!) {
                if (itemRoom.idRoom == bill?.roomId) {
                    room = itemRoom
                }
            }
            showLoading(false)
            initUi()
        })
    }

    private fun onClick() {
        binding!!.apply {
            tvBook.setOnClickListener {
                APIHelper().deleteBooking(bill?.billId!!, onPre = {
                    showLoading(true)
                }, onSuccess = {
                    showLoading(false)
                    if (it.isNotEmpty()){
                        finish()
                    } else {
                        Toast.makeText(this@BookingDetailActivity, "Fail", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            icBack.setOnClickListener { finish() }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.appBar.visibility = View.GONE
            binding!!.layoutContent.visibility = View.GONE
            binding!!.layoutBook.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.appBar.visibility = View.VISIBLE
            binding!!.layoutContent.visibility = View.VISIBLE
            binding!!.layoutBook.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }
}
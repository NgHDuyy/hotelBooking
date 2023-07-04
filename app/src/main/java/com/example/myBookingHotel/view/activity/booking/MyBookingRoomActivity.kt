package com.example.myBookingHotel.view.activity.booking

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.ActivityMyBookingRoomBinding
import com.example.myBookingHotel.model.booking.Bill
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.booking.BookingAdapter
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class MyBookingRoomActivity : BaseActivity() {

    private var binding: ActivityMyBookingRoomBinding? = null
    private var listBooking: ArrayList<Bill> = arrayListOf()
    private var bookingAdapter: BookingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookingRoomBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initUi()
        onClick()
        getListBooking()
    }

    private fun initUi() {

        bookingAdapter = BookingAdapter(this@MyBookingRoomActivity, listBooking, preferenceHelper) {
            val bill = listBooking[it]
            val bundle = Bundle()
            bundle.putString("bill", Gson().toJson(bill))
            val intent = Intent(this@MyBookingRoomActivity, BookingDetailActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding!!.rcvMyBooking.apply {
            layoutManager = LinearLayoutManager(this@MyBookingRoomActivity)
            adapter = bookingAdapter
        }
    }

    private fun onClick() {
        binding!!.apply {
            icBack.setOnClickListener { finish() }
            srRcv.setOnRefreshListener {
                getListBooking()
                binding!!.srRcv.isRefreshing = false
            }
        }
    }

    private fun getListBooking() {
        val customerId = preferenceHelper.getInfoUser(0)
        APIHelper().getBookingByCustomerId(customerId = customerId, onPre = {
            showLoading(true)
        }, onSuccess = {
            if (it.isNotEmpty()) {
                listBooking = try {
                    Gson().fromJson(it, object : TypeToken<ArrayList<Bill>>() {}.type)
                } catch (e: JsonSyntaxException) {
                    arrayListOf()
                } ?: arrayListOf()
                bookingAdapter!!.setData(listBooking)
                showLoading(false)
            } else {
                Toast.makeText(this@MyBookingRoomActivity, "fail", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) {
                appBar.visibility = View.GONE
                srRcv.visibility = View.GONE
                pbUpdateData.visibility = View.VISIBLE
            } else {
                appBar.visibility = View.VISIBLE
                srRcv.visibility = View.VISIBLE
                pbUpdateData.visibility = View.GONE
            }
        }
    }
}
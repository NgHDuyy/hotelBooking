package com.example.myBookingHotel.view.activity.find

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.ActivityLocationChoosedDetailBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.room.HotelAdapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class LocationChoosedDetailActivity : BaseActivity() {

    private var binding: ActivityLocationChoosedDetailBinding? = null

    private var listResult: MutableList<Hotel> = mutableListOf()
    private var strLocationSearch: String? = ""
    private var resultSearchAdapter: HotelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationChoosedDetailBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        intent.extras?.let {
            strLocationSearch = it.getString("txt_find")
        }
        initUi()
        onClick()
    }

    private fun onClick() {
        binding!!.icBack.setOnClickListener { finish() }
    }

    private fun initUi() {
        showLoading(true)
        binding!!.tvTxtFind.text = strLocationSearch
        resultSearchAdapter = HotelAdapter(
            context = this,
            preferenceHelper = preferenceHelper,
            listHotel = listResult,
            clickItem = clickItem,
            clickFavorite = clickFavorite
        )
        binding!!.rcvResultSearch.apply {
            layoutManager = LinearLayoutManager(this@LocationChoosedDetailActivity)
            adapter = resultSearchAdapter
        }
        getListResult()
    }

    private fun getListResult() {
        val listHotel: MutableList<Hotel> = try {
            Gson().fromJson(
                preferenceHelper.listHotelLocation,
                object : TypeToken<MutableList<Hotel>>() {}.type
            )
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        } ?: mutableListOf()
        listResult = listHotel.filter {
            it.location?.province == strLocationSearch
        }.toMutableList()
        resultSearchAdapter!!.setNewData(listResult)
        showLoading(false)
    }

    private val clickItem = object : (Int) -> Unit {
        override fun invoke(pos: Int) {

        }

    }

    private val clickFavorite = object : (Int) -> Unit {
        override fun invoke(position: Int) {

        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.layoutContent.visibility = View.GONE
            binding!!.appBar.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.layoutContent.visibility = View.VISIBLE
            binding!!.appBar.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }
}
package com.example.myBookingHotel.view.activity.room

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.myBookingHotel.databinding.ActivityHotelPreviewBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.SlideImageAdapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File


class HotelPreviewActivity : BaseActivity() {

    private var hotel: Hotel? = null
    private var slideImageAdapter: SlideImageAdapter? = null

    private var binding: ActivityHotelPreviewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelPreviewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        showLoading(true)
        loadHotel()
        initUi()
        onClick()
    }

    private fun loadHotel() {
        val hotelJson =
            File(applicationContext.filesDir, globalHelper.HOTEL_FILE_NAME).inputStream()
                .bufferedReader()
                .use { it.readLine() }
        hotel = try {
            Gson().fromJson(hotelJson, Hotel::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
        showLoading(false)
    }

    @SuppressLint("SetTextI18n")
    private fun initUi() {
        if (hotel != null) {
            binding!!.apply {
                viewPageTop.offscreenPageLimit = 3
                viewPageTop.clipToPadding = false
                viewPageTop.clipChildren = false
                slideImageAdapter = SlideImageAdapter(
                    this@HotelPreviewActivity,
                    this@HotelPreviewActivity,
                    hotel?.image
                )
                viewPageTop.adapter = slideImageAdapter
                circleIndicator3.setViewPager(viewPageTop)
                viewPageTop.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        mHandler.removeCallbacks(mRunnable)
                        mHandler.postDelayed(mRunnable, 3000)
                    }
                })
                tvHotelName.text = hotel!!.hotelName
                tvRating.text = hotel!!.star.toString()
                tvRatingTotal.text = "${hotel!!.vote} (${hotel!!.voteTotal})"
                tvLocation.text =
                    "${hotel!!.location?.address},${hotel!!.location?.district},${hotel!!.location?.province}"
                tvPrice.text = "${hotel!!.absPrice} ${preferenceHelper.moneyType()}"
                tvDescription.text =
                    hotel!!.description!!.replace("XXX", hotel!!.hotelName!!).replace("//", "\n")

            }
        }
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mRunnable = Runnable {
        val index: Int = binding?.viewPageTop!!.currentItem
        if (index == hotel?.image!!.size - 1) {
            binding?.viewPageTop!!.currentItem = 0
        } else {
            binding?.viewPageTop!!.currentItem = index + 1
        }
    }

    private fun onClick() {
        binding!!.icBack.setOnClickListener { finish() }
        binding!!.btnChooseRoom.setOnClickListener {
            val hotelJson = Gson().toJson(hotel)
            File(applicationContext.filesDir, globalHelper.HOTEL_FILE_NAME).outputStream()
                .bufferedWriter().use { it.write(hotelJson) }
            val intent = Intent(this, ChooseRoomActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.icBack.visibility = View.GONE
            binding!!.layoutContent.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.icBack.visibility = View.VISIBLE
            binding!!.layoutContent.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }
}
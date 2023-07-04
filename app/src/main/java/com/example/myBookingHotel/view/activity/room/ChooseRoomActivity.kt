package com.example.myBookingHotel.view.activity.room

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.ActivityChooseRoomBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.room.RoomAdapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File

class ChooseRoomActivity : BaseActivity() {

    private var binding: ActivityChooseRoomBinding? = null
    private var hotel: Hotel? = null
    private var roomAdapter: RoomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRoomBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        loadHotel()
        initUi()
        onClick()
    }

    private fun loadHotel() {
        val hotelJson = File(applicationContext.filesDir,globalHelper.HOTEL_FILE_NAME).inputStream().bufferedReader()
            .use { it.readLine() }

        hotel = try {
            Gson().fromJson(hotelJson, Hotel::class.java)
        } catch (e: JsonSyntaxException){
            null
        }
    }

    private fun initUi() {

        if (hotel!= null && hotel!!.listRoom != null){
            roomAdapter = RoomAdapter(this,preferenceHelper, hotel!!.listRoom!!, onBook = onBook )
            binding!!.rcvListRoom.apply {
                layoutManager = LinearLayoutManager(this@ChooseRoomActivity)
                adapter =  roomAdapter
            }
        }

    }

    private fun onClick() {
        binding!!.icBack.setOnClickListener { finish() }
    }

    private var onBook = object : (String) -> Unit {
        override fun invoke(p1: String) {
            if (p1.isNotEmpty()){
                File(applicationContext.filesDir, globalHelper.ROOM_FILE_NAME).outputStream()
                    .bufferedWriter().use { it.write(p1) }
                val intent = Intent(this@ChooseRoomActivity, BookRoomActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
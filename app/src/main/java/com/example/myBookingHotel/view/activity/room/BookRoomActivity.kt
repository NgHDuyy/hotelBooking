package com.example.myBookingHotel.view.activity.room

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ActivityBookRoomBinding
import com.example.myBookingHotel.model.booking.Bill
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.model.hotel.Room
import com.example.myBookingHotel.model.user.Card
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.activity.main.MainActivity
import com.example.myBookingHotel.view.adapter.SlideImageAdapter
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
class BookRoomActivity : BaseActivity() {

    private var binding: ActivityBookRoomBinding? = null

    private var room: Room? = null
    private var hotel: Hotel? = null

    private var typePay: Int = 1
    private var strTypePay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookRoomBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        loadHotel()
        loadRoom()
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
    }

    private fun loadRoom() {
        val roomJson = File(applicationContext.filesDir, globalHelper.ROOM_FILE_NAME).inputStream()
            .bufferedReader()
            .use { it.readLine() }

        room = try {
            Gson().fromJson(roomJson, Room::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    private var slideImageAdapter: SlideImageAdapter? = null
    private fun initUi() {
        showLoading(true)
        binding?.apply {

            viewPageTop.offscreenPageLimit = 3
            viewPageTop.clipToPadding = false
            viewPageTop.clipChildren = false
            slideImageAdapter = SlideImageAdapter(
                this@BookRoomActivity,
                this@BookRoomActivity,
                room?.listURL
            )
            viewPageTop.adapter = slideImageAdapter
            circleIndicator3.setViewPager(viewPageTop)
            viewPageTop.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mHandler.removeCallbacks(mRunnable)
                    mHandler.postDelayed(mRunnable, 3000)
                }
            })
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

            val currentTime = Calendar.getInstance().time
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR_OF_DAY, 6)
            val futureTime = calendar.time

            tvTimeCheckIn.text = dateFormat.format(currentTime)
            tvTimeCheckOut.text = dateFormat.format(futureTime)

            rdPayByCard.isChecked = true
            strTypePay = rdPayByCard.text.toString()

            showLoading(false)
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

    private var dateFormat = SimpleDateFormat("yyyy-MM-dd")

    private fun onClick() {
        binding!!.icBack.setOnClickListener { finish() }
        binding!!.tvBook.setOnClickListener {
            val id = preferenceHelper.getInfoUser(0) + hotel?.hotelId + room?.idRoom
            val bill = Bill(
                id,
                preferenceHelper.getInfoUser(0),
                preferenceHelper.getInfoUser(1),
                preferenceHelper.getInfoUser(3),
                hotel?.hotelId,
                room?.idRoom,
                room?.roomName,
                room?.roomNumber,
                dateFormat.parse(binding!!.tvTimeCheckIn.text.toString()),
                dateFormat.parse(binding!!.tvTimeCheckOut.text.toString()),
                binding!!.tvRoomPrice.text.toString().replace(preferenceHelper.moneyType(), "")
                    .replace(" ", "")
                    .toDouble()
            )
            globalHelper.showConfirmBookDialog(this@BookRoomActivity, onPay = {
                if (typePay == 1) {
                    val strListCard = preferenceHelper.getInfoUser(4)

                    val listCard: MutableList<Card> =
                        Gson().fromJson(strListCard, object : TypeToken<List<Card>>() {}.type)
                            ?: mutableListOf()
                    if (listCard.isNotEmpty()) {
                        globalHelper.showConfirmCardPass(
                            this@BookRoomActivity,
                            listCard,
                            onSuccess = {
                                APIHelper().createBill(bill) {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.txt_booking_success),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    val intent =
                                        Intent(this@BookRoomActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            })
                    } else {
                        Toast.makeText(this, getString(R.string.txt_no_card), Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    APIHelper().createBill(bill) {
                        val intent = Intent(this@BookRoomActivity, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                }
            })
        }
        binding!!.rgPayType.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rd_pay_by_card -> {
                    strTypePay = binding!!.rdPayByCard.text.toString()
                    typePay = 1
                }

                R.id.rd_pay_in_cash -> {
                    strTypePay = binding!!.rdPayInCash.text.toString()
                    typePay = 2
                }
            }
        }
        binding!!.tvTimeCheckIn.setOnClickListener {
            globalHelper.showDateTimeDialog(this@BookRoomActivity, 1, onSuccess = { string ->
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding!!.tvTimeCheckIn.text = string
                try {
                    val date: Date? = format.parse(string)
                    val calendar = Calendar.getInstance()
                    calendar.time = date!!
                    calendar.add(Calendar.DAY_OF_MONTH, 1)

                    val newTimeString: String = format.format(calendar.time)
                    binding!!.tvTimeCheckOut.text = newTimeString
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            })
        }
        binding!!.tvTimeCheckOut.setOnClickListener {
            globalHelper.showDateTimeDialog(this@BookRoomActivity, 2, onSuccess = { string ->
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding!!.tvTimeCheckOut.text = string
                try {
                    val date: Date? = format.parse(string)
                    val calendar = Calendar.getInstance()
                    calendar.time = date!!
                    calendar.add(Calendar.DAY_OF_MONTH, -1)

                    val newTimeString: String = format.format(calendar.time)
                    binding!!.tvTimeCheckIn.text = newTimeString
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            })
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
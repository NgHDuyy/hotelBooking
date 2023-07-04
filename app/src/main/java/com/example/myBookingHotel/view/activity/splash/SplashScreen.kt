package com.example.myBookingHotel.view.activity.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.activity.viewModels
import com.example.myBookingHotel.R
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.model.hotel.Location
import com.example.myBookingHotel.model.hotel.Room
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.activity.main.MainActivity
import com.example.myBookingHotel.view.activity.user.LogInActivity
import com.example.myBookingHotel.viewmodel.liveData.MainViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class SplashScreen : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        initUI()
        checkLogin()
    }

    private fun initUI() {
        checkDataDevice()
    }

    private fun checkDataDevice() {
        if (preferenceHelper.statusBarHeight == 0) // status bar height
            preferenceHelper.statusBarHeight = globalHelper.getStatusBarHeight(this)
        if (preferenceHelper.actionBarHeight == 0) // action bar height
            preferenceHelper.actionBarHeight = globalHelper.getActionBarSize(this)
        if (preferenceHelper.widthScreen == 0 || preferenceHelper.heightScreen == 0) {
            val displayMetrics = DisplayMetrics()
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            preferenceHelper.widthScreen = width

            val height = displayMetrics.heightPixels
            preferenceHelper.heightScreen = height
        }

    }

    private fun checkLogin() {
        Handler().postDelayed({
            if (preferenceHelper.statusLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            }
        }, 1000)
    }
}
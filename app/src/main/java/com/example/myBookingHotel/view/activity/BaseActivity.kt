package com.example.myBookingHotel.view.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myBookingHotel.viewmodel.utils.helper.GlobalHelper
import com.example.myBookingHotel.viewmodel.utils.helper.LocaleHelper
import com.example.myBookingHotel.viewmodel.utils.helper.PreferenceHelper

abstract class BaseActivity: AppCompatActivity() {
    val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(this@BaseActivity) }
    val globalHelper: GlobalHelper by lazy { GlobalHelper() }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
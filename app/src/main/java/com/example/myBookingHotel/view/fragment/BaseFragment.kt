package com.example.myBookingHotel.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myBookingHotel.viewmodel.utils.helper.GlobalHelper
import com.example.myBookingHotel.viewmodel.utils.helper.PreferenceHelper

abstract class BaseFragment : Fragment() {

    protected val preferenceHelper: PreferenceHelper by lazy { PreferenceHelper(requireContext()) }
    protected val globalHelper: GlobalHelper by lazy { GlobalHelper() }
    var hasInitializedRootView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
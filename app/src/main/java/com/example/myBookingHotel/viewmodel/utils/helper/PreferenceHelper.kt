package com.example.myBookingHotel.viewmodel.utils.helper

import android.content.Context
import com.example.myBookingHotel.R
import com.example.myBookingHotel.model.user.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class PreferenceHelper(private val context: Context, name: String = PREFERENCES_NAME) {
    private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    companion object {
        val globalHelper = GlobalHelper()
        val PREFERENCES_NAME = globalHelper.preferenceNameApp
    }


    var widthScreen: Int
        get() {
            return sharedPreferences.getInt(globalHelper.widthScreen, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.widthScreen, value).apply()
        }


    var heightScreen: Int
        get() {
            return sharedPreferences.getInt(globalHelper.heightScreen, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.heightScreen, value).apply()
        }


    var statusBarHeight: Int
        get() {
            return sharedPreferences.getInt(globalHelper.statusBarHeight, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.statusBarHeight, value).apply()
        }

    var actionBarHeight: Int
        get() {
            return sharedPreferences.getInt(globalHelper.actionBarHeight, 0)
        }
        set(value) {
            sharedPreferences.edit().putInt(globalHelper.actionBarHeight, value).apply()
        }


    var profileUser: String
        //thong tin nguoi dung
        get() {
            return sharedPreferences.getString(globalHelper.profileUser, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.profileUser, value).apply()
        }

    fun getInfoUser(value: Int): String {
        if (profileUser.isEmpty()) return ""
        val userProfile: User? = try {
            Gson().fromJson(profileUser, User::class.java)
        } catch (e: JsonSyntaxException) {
            null
        }
        if (value == 0) {
            if (userProfile?.userId != null)
                return userProfile.userId.toString()
        } else if (value == 1) {
            if (userProfile?.name != null)
                return userProfile.name.toString()
        } else if (value == 2) {
            if (userProfile?.email != null)
                return userProfile.email.toString()
        }  else if (value == 3) {
            if (userProfile?.phoneNumber != null)
                return userProfile.phoneNumber.toString()
        }else if (value == 4) {
            if (userProfile?.cards != null)
                return Gson().toJson(userProfile.cards)
        }

        return ""
    }

    var statusLogin: Boolean
        get() {
            return sharedPreferences.getBoolean(globalHelper.statusLogin, false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(globalHelper.statusLogin, value).apply()
        }

    var languageDevice: String
        get() {
            return sharedPreferences.getString(
                globalHelper.languageDevice, context.getString(R.string.txt_language)
            ) ?: context.getString(R.string.txt_language)
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.languageDevice, value).apply()
        }

    var nearLocation: String
        get() {
            return if (districtLocation.isEmpty()) {
                provinceLocation
            } else {
                "$districtLocation, $provinceLocation"
            }
        }
        set(value) {
            if (value.contains(", ")) {
                districtLocation = value.split(", ")[0]
                provinceLocation = value.split(", ")[1]
            }
        }
    var provinceLocation: String
        get() {
            return sharedPreferences.getString(
                globalHelper.provinceLocation,
                context.getString(R.string.txt_Ha_Noi)
            ) ?: context.getString(R.string.txt_Ha_Noi)
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.provinceLocation, value).apply()
        }
    var districtLocation: String
        get() {
            return sharedPreferences.getString(globalHelper.districtLocation, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.districtLocation, value).apply()
        }


    var listHotelLocation: String
        get() {
            return sharedPreferences.getString(globalHelper.listHotelLocation, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.listHotelLocation, value).apply()
        }
    var listHotelFavorite: String
        get() {
            return sharedPreferences.getString(globalHelper.listHotelFavorite, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.listHotelFavorite, value).apply()
        }

    var listRecentSearch: String
        get() {
            return sharedPreferences.getString(globalHelper.listRecentSearch, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.listRecentSearch, value).apply()
        }

    var listProvince: String
        get() {
            return sharedPreferences.getString(globalHelper.listProvince, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(globalHelper.listProvince, value).apply()
        }

    fun moneyType(): String {
        if (languageDevice == "en") return "Usd"
        return "đ"
    }

}
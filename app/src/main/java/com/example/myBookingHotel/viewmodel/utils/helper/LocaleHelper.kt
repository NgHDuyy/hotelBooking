package com.example.myBookingHotel.viewmodel.utils.helper

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import java.util.*

object
LocaleHelper {
    fun onAttach(context: Context?): Context? {
        if (context == null)
            return context
        return setLocale(context)
    }
    private fun setLocale(context: Context): Context {
        val language = PreferenceHelper(context).languageDevice

        val locale = if (language.contains("-")) {
            val langLocal =
                language.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Locale(langLocal[0], langLocal[1])
        } else {
            when (language) {
                "vi" -> Locale(language, "VN")
                else -> Locale(language)
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, locale)
        } else
            updateResourcesLegacy(context, locale)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)

        configuration.setLocale(locale)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        configuration.setLocales(localeList)

        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)

        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }
}
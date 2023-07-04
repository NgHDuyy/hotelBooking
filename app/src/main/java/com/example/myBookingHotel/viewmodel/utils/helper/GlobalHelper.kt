package com.example.myBookingHotel.viewmodel.utils.helper

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.R.attr.actionBarSize
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myBookingHotel.R
import com.example.myBookingHotel.model.user.Card
import com.example.myBookingHotel.view.adapter.card.CardAdapter
import com.example.myBookingHotel.view.adapter.find.ListTextAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.ArrayAdapter as ArrayAdapter1

class GlobalHelper {

    val preferenceNameApp = "com.example.myBookingHotel"
    val DEBUG = "DEBUG"

    val HOTEL_FILE_NAME = "HOTEL_FILE_NAME"
    val ROOM_FILE_NAME = "ROOM_FILE_NAME"

    val widthScreen = "widthScreen"
    val heightScreen = "heightScreen"
    val statusBarHeight = "status_bar_height"
    val actionBarHeight = "action_bar_height"

    val profileUser = "profileUser"
    val statusLogin = "statusLogin"
    val languageDevice = "language_device"
    val provinceLocation = "provinceLocation"
    val districtLocation = "districtLocation"
    val listHotelLocation = "listHotelLocation"
    val listHotelFavorite = "listHotelFavorite"
    val listRecentSearch = "listRecentSearch"
    val listProvince = "listProvince"


    fun slideView(view: View, currentHeight: Int, newHeight: Int, duration: Int) {
        val slideAnimator =
            ValueAnimator.ofInt(currentHeight, newHeight).setDuration(duration.toLong())

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */slideAnimator.addUpdateListener { animation1: ValueAnimator ->
            view.layoutParams.height = (animation1.animatedValue as Int)
            view.requestLayout()
        }

        /*  We use an animationSet to play the animation  */
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.play(slideAnimator)
        animationSet.start()
    }

    fun getStatusBarHeight(activity: Activity): Int {
        var result = 0
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = activity.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getActionBarSize(context: Context): Int {
        val styledAttributes =
            context.theme.obtainStyledAttributes(intArrayOf(actionBarSize))
        val actionBarSize = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return actionBarSize
    }

    fun closeKeyboard(activity: AppCompatActivity) {
        val view = activity.currentFocus
        if (view != null) {
            val imm =
                activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getLanguageApp(context: Context, languageCode: String?): String {
        when (languageCode) {
            "en" -> return context.resources.getString(R.string.english)
            "vi" -> return context.resources.getString(R.string.vietnam)
        }
        return context.resources.getString(R.string.vietnam)
    }

    fun showDialogLanguage(
        activity: Activity?, languageDevice: String, languageListener: (String?) -> Unit
    ) {
        if (activity == null) return
        val builder = AlertDialog.Builder(activity, R.style.bottom_bottom_dialog)
        val mView = activity.layoutInflater.inflate(R.layout.dialog_language, null)
        val rdEnglish = mView.findViewById<AppCompatRadioButton>(R.id.rd_english)
        val rdVietnam = mView.findViewById<AppCompatRadioButton>(R.id.rd_vietnam)
        builder.setView(mView)
        val dialog = builder.create()
        if (dialog.window != null) dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var languageTo = languageDevice
        when (languageDevice) {
            "en" -> rdEnglish.isChecked = true
            "vi" -> rdVietnam.isChecked = true
        }
        rdEnglish.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                languageTo = "en"
                dialog.dismiss()
            }
        }
        rdVietnam.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                languageTo = "vi"
                dialog.dismiss()
            }
        }
        dialog.setOnDismissListener {
            languageListener.invoke(languageTo)
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    fun showFindLocationDialog(
        context: Context?,
        strNearMe: String,
        countries: ArrayList<String>,
        clickItemListener: (String) -> Unit
    ) {

        if (context == null) return

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_find_location)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val close = dialog.findViewById<ImageView>(R.id.ic_close)
        val atvFindHotel = dialog.findViewById<AutoCompleteTextView>(R.id.atv_find_hotel)
        val tvNearMe = dialog.findViewById<TextView>(R.id.tv_near_me)

        tvNearMe.text = "Gần tôi ($strNearMe)"
        atvFindHotel.setAdapter(
            ArrayAdapter1(
                context,
                R.layout.custom_dropdown_item,
                countries
            )
        )
        close.setOnClickListener { dialog.dismiss() }

        var strCallBack = ""
        tvNearMe.setOnClickListener {
            strCallBack = strNearMe
            dialog.dismiss()
        }
        atvFindHotel.setOnItemClickListener { parent, view, position, id ->
            strCallBack = parent.getItemAtPosition(position).toString()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            clickItemListener.invoke(strCallBack)
        }

        dialog.show()

    }

    fun showChooseLocationDialog(
        activity: Activity?,
        context: Context,
        list: ArrayList<String>,
        onItemClick: (String) -> Unit
    ) {
        if (activity == null) return
        val builder = AlertDialog.Builder(activity, R.style.bottom_bottom_dialog)
        val mView = activity.layoutInflater.inflate(R.layout.dialog_choose_location, null)
        val rcvChooseLocation = mView.findViewById<RecyclerView>(R.id.rcv_choose_location)
        builder.setView(mView)
        val dialog = builder.create()

        dialog.setCancelable(true)

        val adapterString = ListTextAdapter(context, list, object : (String) -> Unit {
            override fun invoke(strProvince: String) {

            }
        })
        rcvChooseLocation.layoutManager = LinearLayoutManager(context)
        rcvChooseLocation.adapter = adapterString

        dialog.show()
    }

    fun showDateTimeDialog(context: Context, type: Int, onSuccess: (String) -> Unit) {
        val selectedDateTime = if (type == 1) {
            Calendar.getInstance()
        } else {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR_OF_DAY, 6)
            calendar
        }

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, monthOfYear)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val selectedDateTimeString =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                        selectedDateTime.time
                    )
                onSuccess.invoke(selectedDateTimeString)
            },
            selectedDateTime.get(Calendar.YEAR),
            selectedDateTime.get(Calendar.MONTH),
            selectedDateTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    fun showConfirmBookDialog(context: Context?, onPay: () -> Unit) {

        if (context == null) return

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_book)
        dialog.setCancelable(false)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val attr = window.attributes
        attr.gravity = Gravity.CENTER
        window.attributes = attr

        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancle)
        val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnConfirm.setOnClickListener {
            onPay.invoke()
            dialog.dismiss()
        }

        dialog.show()

    }

    fun showConfirmCardPass(
        context: Context?,
        listCard: MutableList<Card>?,
        onSuccess: () -> Unit
    ) {

        if (context == null) return

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_pay_card)
        dialog.setCancelable(false)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val attr = window.attributes
        attr.gravity = Gravity.CENTER
        window.attributes = attr
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val rcvCard = dialog.findViewById<RecyclerView>(R.id.rcv_card)

        rcvCard.layoutManager = LinearLayoutManager(context)
        rcvCard.adapter = CardAdapter(
            context,
            listCard
        ) {
            showConfirmPinCode(context, it.pinCode) {
                onSuccess.invoke()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showConfirmPinCode(context: Context?, pinCode: String?, onSuccess: () -> Unit) {

        if (context == null) return

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_pincode)
        dialog.setCancelable(false)

        val window = dialog.window ?: return

        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        val attr = window.attributes
        attr.gravity = Gravity.CENTER
        window.attributes = attr
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val edtPinCode = dialog.findViewById<EditText>(R.id.edt_pin_code)
        val btnCancel = dialog.findViewById<TextView>(R.id.btn_cancle)
        val btnConfirm = dialog.findViewById<TextView>(R.id.btn_confirm)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnConfirm.setOnClickListener {
            if (edtPinCode.text.toString().trim() == pinCode) {
                onSuccess.invoke()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.txt_err_pin_code),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        dialog.show()
    }
}
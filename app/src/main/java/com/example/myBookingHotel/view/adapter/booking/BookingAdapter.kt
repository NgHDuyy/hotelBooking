package com.example.myBookingHotel.view.adapter.booking

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ItemBookingPreviewBinding
import com.example.myBookingHotel.model.booking.Bill
import com.example.myBookingHotel.viewmodel.utils.helper.PreferenceHelper
import java.text.SimpleDateFormat

class BookingAdapter(
    private val context: Context,
    private var listBook: ArrayList<Bill>,
    private val preferenceHelper: PreferenceHelper,
    private val onItemClick : (Int) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setData(listBook: ArrayList<Bill>){
        this.listBook = listBook
        notifyDataSetChanged()
    }

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBookingPreviewBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        return BookingViewHolder(LayoutInflater.from(context).inflate(R.layout.item_booking_preview, parent, false))
    }

    override fun getItemCount(): Int = listBook.size

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val bill = listBook[position]

        holder.binding.apply {
            tvRoomName.text = bill.roomName
            tvRoomNumber.text = bill.roomNumber

            tvTimeCheckIn.text = SimpleDateFormat("yyyy-MM-dd").format(bill.checkIn!!)
            tvTimeCheckOut.text = SimpleDateFormat("yyyy-MM-dd").format(bill.checkOut!!)
            tvRoomPrice.text = bill.totalPrice!!.toLong().toString() + " " + preferenceHelper.moneyType()

            root.setOnClickListener {
                onItemClick.invoke(position)
            }
        }
    }
}
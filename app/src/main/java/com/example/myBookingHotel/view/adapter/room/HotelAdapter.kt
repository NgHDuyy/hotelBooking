package com.example.myBookingHotel.view.adapter.room

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.LayoutItemHotelBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.viewmodel.utils.helper.GlobalHelper
import com.example.myBookingHotel.viewmodel.utils.helper.PreferenceHelper


class HotelAdapter(
    private val context: Context,
    private val preferenceHelper: PreferenceHelper,
    private var listHotel: MutableList<Hotel>,
    private val clickItem: (Int) -> Unit,
    private val clickFavorite: (Int) -> Unit
) :
    RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(listHotel: MutableList<Hotel>) {
        this.listHotel = listHotel
        notifyDataSetChanged()
    }

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutItemHotelBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        return HotelViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_hotel, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listHotel.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = listHotel[position]

        holder.binding.apply {

            val imageBytes: ByteArray = Base64.decode(hotel.image!![0], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            Glide.with(context).load(bitmap)
                .into(imgItem)

            tvHotelName.text = hotel.hotelName
            tvPrice.text = hotel.absPrice.toString() + " " + preferenceHelper.moneyType()
            tvLocation.text = hotel.location!!.district ?: ""
            tvRatingStar.text = hotel.star.toString()
            tvRatingTotal.text = "${hotel.vote!!} (${hotel.voteTotal!!})"
            tvBedDes.text = ""
            layoutCard.setOnClickListener {
                clickItem.invoke(position)
            }
            imgFavorite.setColorFilter(
                ContextCompat.getColor(
                    context,
                    if (hotel.isFavorite) {
                        R.color.colorRed
                    } else {
                        R.color.colorGray
                    }
                ), PorterDuff.Mode.SRC_IN
            )
            imgFavorite.setOnClickListener {
                imgFavorite.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        if (hotel.isFavorite) {
                            R.color.colorGray
                        } else {
                            R.color.colorRed
                        }
                    ), PorterDuff.Mode.SRC_IN
                )
                hotel.isFavorite = !hotel.isFavorite
                clickFavorite.invoke(position)
            }
        }
    }
}
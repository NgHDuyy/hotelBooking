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

class FavoriteHotelAdapter(
    private val context: Context,
    private var listFavorite: MutableList<Hotel>,
    private val clickItem: (Int) -> Unit,
    private val clickFavorite: (Int) -> Unit
) :
    RecyclerView.Adapter<FavoriteHotelAdapter.HotelViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(listFavoriteHotel: MutableList<Hotel>) {
        this.listFavorite = listFavoriteHotel
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
        return listFavorite.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = listFavorite[position]

        holder.binding.apply {
            val imageBytes: ByteArray = Base64.decode(hotel.image!![0], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            Glide.with(context).load(bitmap)
                .into(imgItem)
            tvHotelName.text = hotel.hotelName
            tvPrice.text = hotel.absPrice.toString()
            tvLocation.text = hotel.location!!.district ?: ""
            tvRatingTotal.text = "${hotel.vote!!} (${hotel.voteTotal!!})"
            root.setOnClickListener {
                clickItem.invoke(position)
            }
            imgFavorite.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.colorRed
                ), PorterDuff.Mode.SRC_IN
            )
            imgFavorite.setOnClickListener {
                clickFavorite.invoke(position)
            }
        }
    }
}
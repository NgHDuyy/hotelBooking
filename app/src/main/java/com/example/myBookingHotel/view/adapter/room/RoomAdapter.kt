package com.example.myBookingHotel.view.adapter.room

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ItemRoomBinding
import com.example.myBookingHotel.model.hotel.Room
import com.example.myBookingHotel.viewmodel.utils.helper.PreferenceHelper
import com.google.gson.Gson

class RoomAdapter(
    private val context: Context,
    private val preferenceHelper: PreferenceHelper,
    private var listRoom: List<Room>,
    private val onBook: (String) -> Unit
) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRoomBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_room, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listRoom.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = listRoom[position]

        holder.binding.apply {
            val imageBytes: ByteArray = Base64.decode(room.listURL!![0], Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            Glide.with(context).load(bitmap)
                .into(imgRoom)
            tvRoomName.text = room.roomName
            tvRoomNumber.text = room.roomNumber
            val strBedType =
                if (room.numberBed == 1) context.getString(R.string.single_bed) else context.getString(
                    R.string.double_bed
                )
            tvTotalBed.text = "${context.getString(R.string.txt_type_bed)}: $strBedType"
            tvQuantityPeople.text = "${context.getString(R.string.txt_quantity_people)}: ${room.maximumPeople}"
            tvRoomPrice.text = "${room.price} ${preferenceHelper.moneyType()}"
            tvRoomDes.text = room.description
            tvRoomDes.visibility = View.GONE
            tvSeeMore.setOnClickListener {
                if (tvRoomDes.visibility == View.GONE) {
                    tvRoomDes.visibility = View.VISIBLE
                } else {
                    tvRoomDes.visibility = View.GONE
                }
            }

            if (!room.isEmpty) {
                btnBook.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorGray))
            } else {
                btnBook.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorOrange))
                btnBook.setOnClickListener {
                    onBook.invoke(Gson().toJson(room))
                }
            }

        }
    }
}
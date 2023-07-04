package com.example.myBookingHotel.view.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ItemSlideImageBinding


class SlideImageAdapter(private val context: Context?, private val activity: Activity?,private var mImageList: List<String>? = null): RecyclerView.Adapter<SlideImageAdapter.SlideImageViewHolder>() {

    fun setData(mImageList: List<String>?) {
        this.mImageList = mImageList
        notifyDataSetChanged()
    }

    class SlideImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemSlideImageBinding = ItemSlideImageBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideImageViewHolder {
        return SlideImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_slide_image, parent, false))
    }

    override fun getItemCount(): Int = mImageList!!.size

    override fun onBindViewHolder(holder: SlideImageViewHolder, position: Int) {
        val strImage = mImageList!![position]
        val imageBytes: ByteArray = Base64.decode(strImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Glide.with(activity!!).load(bitmap)
            .into(holder.binding.imgView)
    }

}
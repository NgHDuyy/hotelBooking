package com.example.myBookingHotel.view.adapter.find

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ItemRecentSearchBinding

class ListTextAdapter(
    private val context: Context,
    private var listLocationSearch: MutableList<String>,
    private val clickItem: (String) -> Unit
) : RecyclerView.Adapter<ListTextAdapter.ListTextViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(listLocationSearch: MutableList<String>) {
        this.listLocationSearch = listLocationSearch
        notifyDataSetChanged()
    }

    class ListTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecentSearchBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTextViewHolder {
        return ListTextViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listLocationSearch.size
    }

    override fun onBindViewHolder(holder: ListTextViewHolder, position: Int) {
        val strItem = listLocationSearch[position]
        holder.binding.tvItemSeach.text = strItem
        holder.binding.tvItemSeach.setOnClickListener {
            clickItem.invoke(strItem)
        }
    }
}
package com.example.myBookingHotel.view.adapter.find

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private val context: Context,
    private var listRecentSearch: MutableList<String>,
    private val clickItem: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(listRecentSearch: MutableList<String>){
        this.listRecentSearch = listRecentSearch
        notifyDataSetChanged()
    }

    class RecentSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecentSearchBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        return RecentSearchViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listRecentSearch.size
    }

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val strItem = listRecentSearch[position]
        holder.binding.tvItemSeach.text = strItem
        holder.binding.tvItemSeach.setOnClickListener {
            clickItem.invoke(strItem)
        }
    }
}
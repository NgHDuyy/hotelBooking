package com.example.myBookingHotel.view.adapter.card

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.LayoutCardItemBinding
import com.example.myBookingHotel.model.user.Card

class CardAdapter(
    private val context: Context,
    private val listCards: MutableList<Card>?,
    private val onClickCard: (Card) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCardItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_card_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listCards!!.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        if (!listCards.isNullOrEmpty()) {
            val card = listCards[position]
            holder.binding.tvBankName.text = card.bankName
            holder.binding.tvName.text = card.name
            holder.binding.tvCardNumber.text = card.cardNumber
            holder.binding.root.setOnClickListener {
                onClickCard.invoke(card)
            }
        }
    }
}
package com.demo.foodorderanddeliveryappkotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.foodorderanddeliveryappkotlin.R
import com.demo.foodorderanddeliveryappkotlin.models.Order

class OrdersHistoryAdapter(
    private val context: Context,
    private val orderList: List<Order>
) : RecyclerView.Adapter<OrdersHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.textViewPhoneNumber)
        private val itemNameTextView: TextView = itemView.findViewById(R.id.textViewItem)
        private val priceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
        private val restaurantTextView: TextView = itemView.findViewById(R.id.textViewRestaurant)

        fun bind(order: Order) {
            phoneNumberTextView.text = order.phoneNumber
            itemNameTextView.text = order.itemName
            priceTextView.text = "₽${order.price}"
            restaurantTextView.text = order.restaurant
        }
    }
}

package com.demo.foodorderanddeliveryappkotlin

import com.demo.foodorderanddeliveryappkotlin.models.Menus

object CartManager {
    private val itemsInTheCartList: MutableList<Menus?> = mutableListOf()

    fun addToCart(menu: Menus) {
        val existingItem = itemsInTheCartList.find { it?.name == menu.name }
        if (existingItem != null) {
            existingItem.totalInCart += 0
        } else {
            itemsInTheCartList.add(menu)
        }
    }

    fun removeFromCart(menu: Menus) {
        val existingItem = itemsInTheCartList.find { it?.name == menu.name }
        if (existingItem != null) {
            if (existingItem.totalInCart > menu.totalInCart) {
                existingItem.totalInCart -= menu.totalInCart
            } else {
                itemsInTheCartList.remove(existingItem)
            }
        }
    }

    fun getItemsInCart(): List<Menus?> {
        return itemsInTheCartList
    }

    fun getTotalItemCount(): Int {
        return itemsInTheCartList.sumBy { it?.totalInCart ?: 0 }
    }

    fun clearCart() {
        itemsInTheCartList.clear()
    }
}

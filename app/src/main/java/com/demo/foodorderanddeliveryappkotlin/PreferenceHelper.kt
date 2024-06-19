package com.demo.foodorderanddeliveryappkotlin

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREFERENCE_NAME = "user_preferences"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private fun getKey(userId: String, key: String): String {
        return "${userId}_$key"
    }

    fun saveName(context: Context, userId: String, name: String) {
        getPreferences(context).edit().putString(getKey(userId, "name"), name).apply()
    }

    fun getName(context: Context, userId: String): String? {
        return getPreferences(context).getString(getKey(userId, "name"), null)
    }

    fun saveAddress(context: Context, userId: String, address: String) {
        getPreferences(context).edit().putString(getKey(userId, "address"), address).apply()
    }

    fun getAddress(context: Context, userId: String): String? {
        return getPreferences(context).getString(getKey(userId, "address"), null)
    }

    fun saveCity(context: Context, userId: String, city: String) {
        getPreferences(context).edit().putString(getKey(userId, "city"), city).apply()
    }

    fun getCity(context: Context, userId: String): String? {
        return getPreferences(context).getString(getKey(userId, "city"), null)
    }

    fun saveCardNumber(context: Context, userId: String, cardNumber: String) {
        getPreferences(context).edit().putString(getKey(userId, "card_number"), cardNumber).apply()
    }

    fun getCardNumber(context: Context, userId: String): String? {
        return getPreferences(context).getString(getKey(userId, "card_number"), null)
    }

    fun saveCardExpiry(context: Context, userId: String, cardExpiry: String) {
        getPreferences(context).edit().putString(getKey(userId, "card_expiry"), cardExpiry).apply()
    }

    fun getCardExpiry(context: Context, userId: String): String? {
        return getPreferences(context).getString(getKey(userId, "card_expiry"), null)
    }
}

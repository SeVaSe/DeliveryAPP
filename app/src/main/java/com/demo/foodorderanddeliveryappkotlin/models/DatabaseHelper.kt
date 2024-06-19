package com.demo.foodorderanddeliveryappkotlin.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class OrderItem(val itemName: String, val price: Double)

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FoodyApp.db"
        private const val TABLE_USERS = "users"
        private const val TABLE_ORDERS = "orders"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ITEM_NAME = "item_name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_RESTAURANT = "restaurant"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_PHONE TEXT," +
                "$COLUMN_PASSWORD TEXT)")
        val createOrdersTable = ("CREATE TABLE $TABLE_ORDERS " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_PHONE TEXT," + // Изменение здесь
                "$COLUMN_ITEM_NAME TEXT," +
                "$COLUMN_PRICE REAL," +
                "$COLUMN_RESTAURANT TEXT)")
        db?.execSQL(createUsersTable)
        db?.execSQL(createOrdersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        onCreate(db)
    }

    fun addUser(name: String, email: String, phone: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
        }
        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success != -1L
    }

    fun addOrder(phone: String, orderItems: List<OrderItem>, restaurant: String): Boolean {
        val db = this.writableDatabase
        var isSuccess = false

        db.beginTransaction()

        try {
            for (orderItem in orderItems) {
                val values = ContentValues().apply {
                    put(COLUMN_PHONE, phone)
                    put(COLUMN_ITEM_NAME, orderItem.itemName)
                    put(COLUMN_PRICE, orderItem.price)
                    put(COLUMN_RESTAURANT, restaurant)
                }
                val success = db.insert(TABLE_ORDERS, null, values)
                isSuccess = success != -1L
            }
            if (isSuccess) {
                db.setTransactionSuccessful()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }

        return isSuccess
    }



    fun authenticateUser(identifier: String, password: String): User? {
        val db = this.readableDatabase
        val selection = "($COLUMN_PHONE = ? OR $COLUMN_EMAIL = ?) AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(identifier, identifier, password)
        val cursor = db.query(
            TABLE_USERS, null, selection, selectionArgs,
            null, null, null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val emailIndex = cursor.getColumnIndex(COLUMN_EMAIL)
            val phoneIndex = cursor.getColumnIndex(COLUMN_PHONE)

            val id = cursor.getInt(idIndex)
            val name = cursor.getString(nameIndex)
            val email = cursor.getString(emailIndex)
            val phone = cursor.getString(phoneIndex)

            user = User(id, name, email, phone)
        }
        cursor.close()
        return user
    }


    fun getUserDataByPhone(identifier: String): User? {
        val db = this.readableDatabase
        val selection = "$COLUMN_PHONE = ? OR $COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf(identifier, identifier)
        val cursor = db.query(
            TABLE_USERS, null, selection, selectionArgs,
            null, null, null
        )
        var user: User? = null
        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val emailIndex = cursor.getColumnIndex(COLUMN_EMAIL)
            val phoneIndex = cursor.getColumnIndex(COLUMN_PHONE)

            val id = cursor.getInt(idIndex)
            val name = cursor.getString(nameIndex)
            val email = cursor.getString(emailIndex)
            val phone = cursor.getString(phoneIndex)

            user = User(id, name, email, phone)
        }
        cursor?.close()
        return user
    }


    fun getOrdersForUser(phone: String): MutableList<Order> {
        val orders = mutableListOf<Order>()
        val db = this.readableDatabase

        val selection = "$COLUMN_PHONE = ?"
        val selectionArgs = arrayOf(phone)

        val cursor = db.query(
            TABLE_ORDERS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.apply {
            if (moveToFirst()) {
                val itemNameIndex = getColumnIndexOrThrow(COLUMN_ITEM_NAME)
                val priceIndex = getColumnIndexOrThrow(COLUMN_PRICE)
                val restaurantIndex = getColumnIndexOrThrow(COLUMN_RESTAURANT)

                do {
                    val itemName = getString(itemNameIndex)
                    val price = getDouble(priceIndex)
                    val restaurant = getString(restaurantIndex)
                    orders.add(Order(phone, itemName, price, restaurant))
                } while (moveToNext())
            }
            close()
        }
        db.close()

        return orders
    }







    // Другие методы для работы с базой данных, такие как получение информации о пользователе и заказах и т. д.
}

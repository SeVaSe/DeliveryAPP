package com.demo.foodorderanddeliveryappkotlin
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.foodorderanddeliveryappkotlin.adapter.OrdersHistoryAdapter
import com.demo.foodorderanddeliveryappkotlin.models.DatabaseHelper
import com.demo.foodorderanddeliveryappkotlin.models.Order
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.activity_order_history.*

class OrdersHistoryActivity : AppCompatActivity() {

    private lateinit var orderList: MutableList<Order>
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var orderHistoryAdapter: OrdersHistoryAdapter
    private var currentUserPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("История заказов")

        currentUserPhone = intent.getStringExtra("PHONE") ?: ""
        databaseHelper = DatabaseHelper(this)
        orderList = mutableListOf()
        Toast.makeText(this, currentUserPhone, Toast.LENGTH_SHORT).show()

        // Получаем список заказов для текущего пользователя из базы данных
        orderList = databaseHelper.getOrdersForUser(currentUserPhone) // Предположим, что у вас есть метод getOrdersForUser

        // Добавим отладочное сообщение для проверки количества заказов в списке
        Log.d("OrdersHistoryActivity", "Number of orders: ${orderList.size}")

        // Инициализируем RecyclerView и устанавливаем адаптер
        recyclerViewOrderHistory.layoutManager = LinearLayoutManager(this)
        orderHistoryAdapter = OrdersHistoryAdapter(this, orderList)
        recyclerViewOrderHistory.adapter = orderHistoryAdapter


    }
}

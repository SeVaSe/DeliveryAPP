package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.foodorderanddeliveryappkotlin.adapter.PlaceYourOrderAdapter
import com.demo.foodorderanddeliveryappkotlin.models.DatabaseHelper
import com.demo.foodorderanddeliveryappkotlin.models.OrderItem
import com.demo.foodorderanddeliveryappkotlin.models.RestaurentModel
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.activity_place_your_order.*
import java.util.*

class PlaceYourOrderActivity : AppCompatActivity() {

    var placeYourOrderAdapter: PlaceYourOrderAdapter? = null
    var isDeliveryOn: Boolean = false
    private var currentUserPhone: String = ""
    private var fullPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_your_order)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.title = "Оформление заказа"
        actionbar?.setDisplayHomeAsUpEnabled(true)

        currentUserPhone = intent.getStringExtra("PHONE") ?: ""
        Toast.makeText(this, currentUserPhone, Toast.LENGTH_SHORT).show()

        // Загрузка сохраненных данных
        loadSavedData(currentUserPhone)

        buttonPlaceYourOrder.setOnClickListener {
            onPlaceOrderButtonCLick()
        }

        switchDelivery?.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                inputAddress.visibility = View.VISIBLE
                inputCity.visibility = View.VISIBLE
                tvDeliveryCharge.visibility = View.VISIBLE
                tvDeliveryChargeAmount.visibility = View.VISIBLE
                isDeliveryOn = true
                calculateTotalAmount()
            } else {
                inputAddress.visibility = View.GONE
                inputCity.visibility = View.GONE
                tvDeliveryCharge.visibility = View.GONE
                tvDeliveryChargeAmount.visibility = View.GONE
                isDeliveryOn = false
                calculateTotalAmount()
            }
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_restaurants -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("PHONE", currentUserPhone)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_orders_history -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("PHONE", currentUserPhone)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        initRecyclerView()
        calculateTotalAmount()
    }

    private fun initRecyclerView() {
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        placeYourOrderAdapter = PlaceYourOrderAdapter(CartManager.getItemsInCart())
        cartItemsRecyclerView.adapter = placeYourOrderAdapter
    }

    private fun calculateTotalAmount() {
        var subTotalAmount = 0f
        for (menu in CartManager.getItemsInCart()) {
            subTotalAmount += menu?.price!! * menu?.totalInCart!!
        }
        tvSubtotalAmount.text = "₽" + String.format("%.2f", subTotalAmount)
        if (isDeliveryOn) {
            tvDeliveryChargeAmount.text = "₽279.00" // Фиксированная цена доставки
            subTotalAmount += 279f // Фиксированная цена доставки
        }
        fullPrice = subTotalAmount.toDouble()
        tvTotalAmount.text = "₽" + String.format("%.2f", subTotalAmount)
    }

    private fun onPlaceOrderButtonCLick() {
        if (TextUtils.isEmpty(inputName.text.toString())) {
            inputName.error = "Введите свое имя"
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputAddress.text.toString())) {
            inputAddress.error = "Введите свой адрес"
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCity.text.toString())) {
            inputCity.error = "Введите свой город"
            return
        } else if (TextUtils.isEmpty(inputCardNumber.text.toString())) {
            inputCardNumber.error = "Введите свой номер карты"
            return
        } else if (!isValidCardNumber(inputCardNumber.text.toString())) {
            inputCardNumber.error = "Номер карты должен содержать 16 цифр"
            return
        } else if (TextUtils.isEmpty(inputCardExpiry.text.toString())) {
            inputCardExpiry.error = "Введите дату окончания действия"
            return
        } else if (!isValidExpiryDate(inputCardExpiry.text.toString())) {
            inputCardExpiry.error = "Введите корректную дату окончания действия (мм/гг)"
            return
        } else if (TextUtils.isEmpty(inputCardPin.text.toString())) {
            inputCardPin.error = "Введите код cvv"
            return
        }

        // Сохранение данных
        saveData(currentUserPhone)

        // Получаем данные для добавления в таблицу addOrder
        val itemsList = mutableListOf<String>() // Создаем список для хранения названий продуктов
        CartManager.getItemsInCart().forEach { menuItem ->
            val itemName = menuItem?.name // Получаем название текущего продукта
            itemName?.let { itemsList.add(it) } // Добавляем название продукта в список
        }

        val price = fullPrice // Получите реальную цену

        // Добавляем заказ в базу данных
        val databaseHelper = DatabaseHelper(this)
        val itemList = mutableListOf<OrderItem>() // Создаем список товаров в заказе
        itemList.add(OrderItem(itemsList.toString(), price)) // Добавляем товары в список

        val isSuccess = databaseHelper.addOrder(currentUserPhone, itemList, "Общая корзина")

        if (isSuccess) {
            Toast.makeText(this, "Заказ успешно сохранен", Toast.LENGTH_SHORT).show()
            CartManager.clearCart() // Очистить корзину после успешного заказа
            val intent = Intent(this@PlaceYourOrderActivity, SuccessOrderActivity::class.java)
            intent.putExtra("PHONE", currentUserPhone)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Ошибка сохранения заказа", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData(userId: String) {
        PreferenceHelper.saveName(this, userId, inputName.text.toString())
        PreferenceHelper.saveAddress(this, userId, inputAddress.text.toString())
        PreferenceHelper.saveCity(this, userId, inputCity.text.toString())
        PreferenceHelper.saveCardNumber(this, userId, inputCardNumber.text.toString())
        PreferenceHelper.saveCardExpiry(this, userId, inputCardExpiry.text.toString())
    }

    private fun loadSavedData(userId: String) {
        inputName.setText(PreferenceHelper.getName(this, userId))
        inputAddress.setText(PreferenceHelper.getAddress(this, userId))
        inputCity.setText(PreferenceHelper.getCity(this, userId))
        inputCardNumber.setText(PreferenceHelper.getCardNumber(this, userId))
        inputCardExpiry.setText(PreferenceHelper.getCardExpiry(this, userId))
    }

    private fun isValidCardNumber(cardNumber: String): Boolean {
        return cardNumber.length == 16
    }

    private fun isValidExpiryDate(expiryDate: String): Boolean {
        if (!expiryDate.matches(Regex("(0[1-9]|1[0-2])[0-9]{2}"))) {
            return false
        }

        val month = expiryDate.substring(0, 2).toInt()
        val year = "20${expiryDate.substring(2)}".toInt()
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return year > currentYear || (year == currentYear && month >= currentMonth)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

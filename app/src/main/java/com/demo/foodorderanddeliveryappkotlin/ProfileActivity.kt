package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.demo.foodorderanddeliveryappkotlin.models.DatabaseHelper
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private var currentUserPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("Профиль")

        databaseHelper = DatabaseHelper(this)

        // Получаем номер телефона текущего пользователя из Intent
        currentUserPhone = intent.getStringExtra("PHONE") ?: ""
        //Toast.makeText(this, currentUserPhone, Toast.LENGTH_SHORT).show()

        // Получаем данные пользователя из базы данных
        val userData = databaseHelper.getUserDataByPhone(currentUserPhone.toString())

        // Заполняем текстовые поля данными из базы данных
        userData?.let {
            name.text = it.name
            emailId.text = it.email
            mobileNumber.text = it.phone
        }

        // Устанавливаем слушатель кликов на кнопку "Выйти"
        btnLogout.setOnClickListener {
            // Вызываем метод для выхода из аккаунта
            logout()
            finish()
        }

        // Устанавливаем слушатель кликов на FAB для отображения всплывающего меню
        fabPhone.setOnClickListener {
            showPopupMenu(it)
        }

        btnHistory.setOnClickListener {
            val intent = Intent(this, OrdersHistoryActivity::class.java)
            intent.putExtra("PHONE", currentUserPhone)
            startActivity(intent)

        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_restaurants -> {
                    // Открываем активность с ресторанами
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("PHONE", currentUserPhone)
                    startActivity(intent)
                   // finish() // Закрываем LoginActivity
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_orders_history -> {
                    // Открываем активность с историей заказов
                    val intent = Intent(this, PlaceYourOrderActivity::class.java)
                    intent.putExtra("PHONE", currentUserPhone)
                    startActivity(intent)
                    //finish() // Закрываем LoginActivity
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    // Открываем активность с профилем пользователя
//                    startActivity(Intent(this, ProfileActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


    }

    private fun logout() {
        // Удаляем информацию об авторизации
        clearAuthenticationStatus()

        // Открываем активность логина
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Закрываем текущую активность

        Toast.makeText(this, "Вы успешно вышли из аккаунта", Toast.LENGTH_SHORT).show()
    }

    private fun clearAuthenticationStatus() {
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("is_authenticated", false)
        editor.putString("user_phone", "")
        editor.apply()
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_call -> {
                    // Открыть звонилку с номером телефона текущего пользователя
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:+7 968 954 5103")
                    startActivity(intent)
                    true
                }
                R.id.menu_email -> {
                    // Открыть приложение почты для отправки письма на адрес электронной почты текущего пользователя
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:foodyappagent@gmail.com")
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


}

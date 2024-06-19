package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo.foodorderanddeliveryappkotlin.R
import com.demo.foodorderanddeliveryappkotlin.models.DatabaseHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)

        // Проверяем состояние авторизации при запуске активити
        checkAuthenticationStatus()

        btnLogin.setOnClickListener {
            val identifier = etMobileNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()

            val user = databaseHelper.authenticateUser(identifier, password)
            if (user != null) {
                Toast.makeText(this, "Вход выполнен успешно", Toast.LENGTH_SHORT).show()
                // Сохраняем информацию об авторизации
                saveAuthenticationStatus(user.phone)
                // Открываем активность входа
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("PHONE", user.phone)
                startActivity(intent)
                finish() // Закрываем LoginActivity
            } else {
                Toast.makeText(this, "Ошибка входа. Проверьте введенные данные", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun checkAuthenticationStatus() {
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isAuthenticated = sharedPrefs.getBoolean("is_authenticated", false)
        val currentUserPhone = sharedPrefs.getString("user_phone", "")

        if (isAuthenticated && !currentUserPhone.isNullOrEmpty()) {
            // Пользователь уже аутентифицирован, переходим к профилю
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("PHONE", currentUserPhone)
            startActivity(intent)
            finish() // Закрываем LoginActivity
        }
    }

    private fun saveAuthenticationStatus(phone: String) {
        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("is_authenticated", true)
        editor.putString("user_phone", phone)
        editor.apply()
    }
}


package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo.foodorderanddeliveryappkotlin.R
import com.demo.foodorderanddeliveryappkotlin.models.DatabaseHelper
import kotlinx.android.synthetic.main.activity_registr.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registr)

        databaseHelper = DatabaseHelper(this)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmailId.text.toString().trim()
            val phone = etMobileNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConPassword.text.toString().trim()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Пароль должен состоять из латинских букв и быть не менее 6 символов", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(phone)) {
                Toast.makeText(this, "Введите корректный номер телефона (например, +79991234567 или 89991234567)", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            } else {
                val isSuccess = databaseHelper.addUser(name, email, phone, password)
                if (isSuccess) {
                    Toast.makeText(this, "Пользователь зарегистрирован", Toast.LENGTH_SHORT).show()
                    // Закрываем текущую активность
                    finish()
                    // Открываем активность входа
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneRegex = "^(\\+7|7|8)\\d{10}\$"
        return phoneNumber.matches(phoneRegex.toRegex())
    }

}

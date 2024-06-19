package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.foodorderanddeliveryappkotlin.adapter.RestaurantListAdapter
import com.demo.foodorderanddeliveryappkotlin.models.RestaurentModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import java.io.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), RestaurantListAdapter.RestaurantListClickListener {

    private var currentUserPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currentUserPhone = intent.getStringExtra("PHONE") ?: ""
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("Список продуктов")

        val restaurantModel = getRestaurantData()
        initRecyclerView(restaurantModel)









        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_restaurants -> {
                    // Открываем активность с ресторанами
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
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("PHONE", currentUserPhone)
                    startActivity(intent)
                    //finish() // Закрываем LoginActivity
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

    }



    private fun initRecyclerView(restaurantList: List<RestaurentModel?>?) {
        val recyclerViewRestaurant = findViewById<RecyclerView>(R.id.recyclerViewRestaurant)
        recyclerViewRestaurant.layoutManager = LinearLayoutManager(this)
        val adapter = RestaurantListAdapter(restaurantList, this)
        recyclerViewRestaurant.adapter =adapter
    }

    private fun getRestaurantData(): List<RestaurentModel?>? {
        val inputStream: InputStream = resources.openRawResource(R.raw.restaurent)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var n : Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)

            }

        }catch (e: Exception){}
        val jsonStr: String = writer.toString()
        val gson = Gson()
        val restaurantModel = gson.fromJson<Array<RestaurentModel>>(jsonStr, Array<RestaurentModel>::class.java).toList()

        return restaurantModel
    }

    override fun onItemClick(restaurantModel: RestaurentModel) {
       val intent = Intent(this@MainActivity, RestaurantMenuActivity::class.java)
        intent.putExtra("RestaurantModel", restaurantModel)
        intent.putExtra("PHONE", currentUserPhone)
        startActivity(intent)
        // finish() // Закрываем LoginActivity

    }
}
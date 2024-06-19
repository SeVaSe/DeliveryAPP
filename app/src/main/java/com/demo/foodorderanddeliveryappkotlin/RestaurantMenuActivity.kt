package com.demo.foodorderanddeliveryappkotlin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.demo.foodorderanddeliveryappkotlin.adapter.MenuListAdapter
import com.demo.foodorderanddeliveryappkotlin.models.Menus
import com.demo.foodorderanddeliveryappkotlin.models.RestaurentModel
import kotlinx.android.synthetic.main.activity_restaurant_menu.*

class RestaurantMenuActivity : AppCompatActivity(), MenuListAdapter.MenuListClickListener {

    private var menuList: List<Menus?>? = null
    private var filteredMenuList: List<Menus?>? = null
    private var menuListAdapter: MenuListAdapter? = null
    private var currentUserPhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        val restaurantModel = intent?.getParcelableExtra<RestaurentModel>("RestaurantModel")
        currentUserPhone = intent.getStringExtra("PHONE") ?: ""
        Toast.makeText(this, currentUserPhone, Toast.LENGTH_SHORT).show()

        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = restaurantModel?.name
        actionBar?.subtitle = restaurantModel?.address
        actionBar?.setDisplayHomeAsUpEnabled(true)

        menuList = restaurantModel?.menus
        filteredMenuList = menuList

        initRecyclerView(filteredMenuList)
        initSearchView()
        initSortSpinner()
    }

    private fun initRecyclerView(menus: List<Menus?>?) {
        menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        menuListAdapter = MenuListAdapter(menus, this)
        menuRecyclerView.adapter = menuListAdapter
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMenuList(newText)
                return true
            }
        })
    }

    private fun initSortSpinner() {
        val sortOptions = arrayOf("По умолчанию", "Сначала дешевые", "Сначала дорогие")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                when (position) {
                    1 -> sortMenuList(true) // Сначала дешевые
                    2 -> sortMenuList(false) // Сначала дорогие
                    else -> menuListAdapter?.updateList(filteredMenuList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterMenuList(query: String?) {
        filteredMenuList = if (query.isNullOrEmpty()) {
            menuList
        } else {
            menuList?.filter { it?.name?.contains(query, true) == true }
        }
        menuListAdapter?.updateList(filteredMenuList)
    }

    private fun sortMenuList(ascending: Boolean) {
        filteredMenuList = if (ascending) {
            filteredMenuList?.sortedBy { it?.price }
        } else {
            filteredMenuList?.sortedByDescending { it?.price }
        }
        menuListAdapter?.updateList(filteredMenuList)
    }

    override fun addToCartClickListener(menu: Menus) {
        CartManager.addToCart(menu)
        Toast.makeText(this, "Добавлено в корзину", Toast.LENGTH_SHORT).show()
    }

    override fun updateCartClickListener(menu: Menus) {
        CartManager.addToCart(menu)
    }

    override fun removeFromCartClickListener(menu: Menus) {
        CartManager.removeFromCart(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@RestaurantMenuActivity, MainActivity::class.java)
                intent.putExtra("PHONE", currentUserPhone)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

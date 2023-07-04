package com.example.myBookingHotel.view.activity.find

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.ActivityFindLocationBinding
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.find.RecentSearchAdapter
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class FindLocationActivity : BaseActivity() {

    private var binding: ActivityFindLocationBinding? = null

    private var txtFind = ""
    private var recentSearchAdapter: RecentSearchAdapter? = null
    private var listProvince: ArrayList<String> = arrayListOf()
    private var listRecentSearch: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindLocationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initUi()
        onClick()
    }

    private fun initUi() {

        recentSearchAdapter = RecentSearchAdapter(context = this, listRecentSearch, clickItem = {
            showResultSearch(it)
        })

        binding!!.rcvFindLocation.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentSearchAdapter
        }
        getListCountries()
        getListRecentSearch()
    }


    private fun getListRecentSearch() {
        listRecentSearch = try {
            val typeToken = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(preferenceHelper.listRecentSearch, typeToken)
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        } ?: mutableListOf()

        listRecentSearch.reverse()
        recentSearchAdapter!!.setNewData(listRecentSearch)
    }


    private fun getListCountries() {
        val typeToken = object : TypeToken<ArrayList<String>>() {}.type
        listProvince = try {
            Gson().fromJson(preferenceHelper.listProvince, typeToken)
        } catch (e: JsonSyntaxException) {
            arrayListOf()
        } ?: arrayListOf()
    }


    private fun onClick() {

        binding!!.icBack.setOnClickListener {
            finish()
        }

        binding!!.layoutFind.setOnClickListener {
            globalHelper.showFindLocationDialog(
                context = this,
                strNearMe = preferenceHelper.nearLocation,
                listProvince,
                clickItemListener = {
                    if (it.isNotEmpty()) {
                        txtFind = it
                        binding!!.tvFindLocation.text = it
                    }
                })
        }

        binding!!.btnFind.setOnClickListener {
            if (txtFind.isNotEmpty()) {
                listRecentSearch.add(txtFind)
                preferenceHelper.listRecentSearch = Gson().toJson(listRecentSearch)
                recentSearchAdapter!!.setNewData(listRecentSearch)
                showResultSearch(txtFind)
            }
        }
    }

    private fun showResultSearch(txtFind: String) {
        val bundle = Bundle()
        bundle.putString("txt_find", txtFind)
        val intent = Intent(this, LocationChoosedDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}
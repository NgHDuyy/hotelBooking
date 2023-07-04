package com.example.myBookingHotel.view.activity.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.example.myBookingHotel.R
import com.example.myBookingHotel.databinding.ActivityMainBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.adapter.home.HomePagerAdapter
import com.example.myBookingHotel.view.fragment.home.AccountFragment
import com.example.myBookingHotel.view.fragment.home.FavoriteFragment
import com.example.myBookingHotel.view.fragment.home.HomeFragment
import com.example.myBookingHotel.view.fragment.home.RecommendFragment
import com.example.myBookingHotel.viewmodel.liveData.MainViewModel
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper
import com.example.myBookingHotel.viewmodel.utils.listener.ScrollCallback
import com.example.myBookingHotel.viewmodel.utils.listener.StringCallback
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var homeFragment: HomeFragment? = null
    private var recommendFragment: RecommendFragment? = null
    private var favoriteFragment: FavoriteFragment? = null
    private var accountFragment: AccountFragment? = null
    private var posTabSelected = 0
    private var posHome = 0
    private var posRecommend = 1
    private val posFavorite = 2
    private var posAccount = 3

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        viewModel.eventOnRestart.observe(this) { event ->
            if (event == MainViewModel.ON_RESTART_APP) {
                viewModel.setEventOnRestart("")
                restartApp()
            }
        }

    }

    private fun initUI() {
        showLoading(true)
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(scrollCallback)
        } else {
            homeFragment!!.setListener(scrollCallback)
        }
        if (recommendFragment == null) {
            recommendFragment = RecommendFragment.newInstance(scrollCallback)
        } else {
            recommendFragment!!.setListener(scrollCallback)
        }
        if (favoriteFragment == null) {
            favoriteFragment = FavoriteFragment.newInstance(scrollCallback)
        } else {
            favoriteFragment!!.setListener(scrollCallback)
        }
        if (accountFragment == null) {
            accountFragment = AccountFragment.newInstance(
                scrollCallback = scrollCallback,
                clickFavoriteListener = ::favoriteFragment
            )
        } else {
            accountFragment!!.setListener(
                scrollCallback = scrollCallback,
                clickFavoriteListener = ::favoriteFragment
            )
        }

        val adapterHome = HomePagerAdapter(supportFragmentManager)
        adapterHome.addPager(homeFragment!!)
        adapterHome.addPager(recommendFragment!!)
        adapterHome.addPager(favoriteFragment!!)
        adapterHome.addPager(accountFragment!!)

        binding.viewPager.apply {
            adapter = adapterHome
            offscreenPageLimit = 0
            setCurrentItem(posTabSelected, false)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        posHome -> {
                            binding.bottomNavigation.selectedItemId = R.id.action_home
                        }

                        posRecommend -> {
                            binding.bottomNavigation.selectedItemId = R.id.action_recommend
                        }

                        posFavorite -> {
                            binding.bottomNavigation.selectedItemId = R.id.action_favorite
                        }

                        posAccount -> {
                            binding.bottomNavigation.selectedItemId = R.id.action_account
                        }
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
        }

        binding.bottomNavigation.apply {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.action_home -> {
                        if (posTabSelected != posHome) {
                            posTabSelected = posHome
                            binding.viewPager.setCurrentItem(posTabSelected, false)
                        }
                        checkNavHide()
                        return@setOnNavigationItemSelectedListener true
                    }

                    R.id.action_recommend -> {
                        if (posTabSelected != posRecommend) {
                            posTabSelected = posRecommend
                            binding.viewPager.setCurrentItem(posTabSelected, false)
                        }
                        checkNavHide()
                        return@setOnNavigationItemSelectedListener true
                    }

                    R.id.action_favorite -> {
                        if (posTabSelected != posFavorite) {
                            posTabSelected = posFavorite
                            binding.viewPager.setCurrentItem(posTabSelected, false)
                        }
                        checkNavHide()
                        return@setOnNavigationItemSelectedListener true
                    }

                    R.id.action_account -> {
                        if (posTabSelected != posAccount) {
                            posTabSelected = posAccount
                            binding.viewPager.setCurrentItem(posTabSelected, false)
                        }
                        checkNavHide()
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                return@setOnNavigationItemSelectedListener false
            }

            selectedItemId = when (posTabSelected) {
                posRecommend -> R.id.action_recommend
                posFavorite -> R.id.action_favorite
                posAccount -> R.id.action_account
                else -> R.id.action_home
            }
        }
        getListHotel()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.viewPager.visibility = View.GONE
            binding.bottomNavigation.visibility = View.GONE
            binding.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding.viewPager.visibility = View.VISIBLE
            binding.bottomNavigation.visibility = View.VISIBLE
            binding.pbUpdateData.visibility = View.GONE
        }
    }

    private fun getListHotel() {
        CoroutineScope(Dispatchers.IO).launch {
            APIHelper().getAllHotel(
                onPre = {
                },
                onPost = object : StringCallback {
                    override fun execute(string: String?) {
                        if (string != null) {
                            preferenceHelper.listHotelLocation = string
                            val listHotelLocation: MutableList<Hotel> = try {
                                Gson().fromJson(
                                    string,
                                    object : TypeToken<MutableList<Hotel>>() {}.type,
                                )
                            } catch (e: JsonSyntaxException) {
                                mutableListOf()
                            } ?: mutableListOf()
                            val listProvince: ArrayList<String> = try {
                                Gson().fromJson(
                                    preferenceHelper.listProvince,
                                    object : TypeToken<ArrayList<String>>() {}.type
                                )
                            } catch (e: JsonSyntaxException) {
                                arrayListOf()
                            } ?: arrayListOf()
                            listHotelLocation.forEach { hotel: Hotel ->
                                if (listProvince.isEmpty()) {
                                    listProvince.add(hotel.location?.province!!)
                                } else {
                                    listProvince.forEach { province ->
                                        if (hotel.location?.province != province) {
                                            listProvince.add(hotel.location?.province!!)
                                        }
                                    }
                                }
                            }
                            preferenceHelper.listProvince = Gson().toJson(listProvince)
                            showLoading(false)
                        }
                    }
                }
            )
        }
    }

    private var actionBarHeight = 0
    private fun checkNavHide() {
        if (isScrollDown) {
            isScrollDown = false
            globalHelper.slideView(binding.bottomNavigation, 0, actionBarHeight, 200)
        }
    }


    private var isScrollDown = false

    private val scrollCallback = object : ScrollCallback {
        override fun execute(scrollDown: Boolean, tabScroll: Int) {
            if (isScrollDown != scrollDown && actionBarHeight > 0) {
                if (tabScroll == posTabSelected) {
                    isScrollDown = scrollDown
                    globalHelper.slideView(
                        binding.bottomNavigation,
                        if (isScrollDown) actionBarHeight else 0,
                        if (isScrollDown) 0 else actionBarHeight,
                        200
                    )
                }
            }
        }
    }

    private fun favoriteFragment() {
        binding.bottomNavigation.selectedItemId = R.id.action_favorite
    }

    private fun restartApp() {
        recreate()
    }

    private var backTime: Long = 0L

    override fun onBackPressed() {
        if (backTime + 2000L > System.currentTimeMillis()){
            super.onBackPressed()
        } else{
            Toast.makeText(this, this@MainActivity.getString(R.string.txt_on_backpress), Toast.LENGTH_LONG).show()
        }
        backTime = System.currentTimeMillis()
    }
}
package com.example.myBookingHotel.view.adapter.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class HomePagerAdapter (fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fragmentList: MutableList<Fragment> = ArrayList()

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return if (position < count) fragmentList[position] else Fragment()
    }


    fun addPager(fragment: Fragment) {
        fragmentList.add(fragment)
    }

}
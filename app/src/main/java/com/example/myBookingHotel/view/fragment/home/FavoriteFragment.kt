package com.example.myBookingHotel.view.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.FragmentFavoriteBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.room.HotelPreviewActivity
import com.example.myBookingHotel.view.adapter.room.FavoriteHotelAdapter
import com.example.myBookingHotel.view.fragment.BaseFragment
import com.example.myBookingHotel.viewmodel.liveData.MainViewModel
import com.example.myBookingHotel.viewmodel.utils.listener.ScrollCallback
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File

class FavoriteFragment : BaseFragment() {

    private var binding: FragmentFavoriteBinding? = null

    private var scrollCallback: ScrollCallback? = null
    private var adapterFavorite: FavoriteHotelAdapter? = null
    private var listFavoriteHotel: MutableList<Hotel> = mutableListOf()

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(scrollCallback: ScrollCallback) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                }
                setListener(scrollCallback)
            }
    }

    fun setListener(scrollCallback: ScrollCallback) {
        this.scrollCallback = scrollCallback
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModel.getEventAddToFavorite.observe(this) {
            listFavoriteHotel.add(it)
            preferenceHelper.listHotelFavorite = Gson().toJson(listFavoriteHotel)
            adapterFavorite!!.setNewData(listFavoriteHotel)
        }

        viewModel.getEventRemoveFavorite.observe(this) {
            listFavoriteHotel.remove(it)
            preferenceHelper.listHotelFavorite = Gson().toJson(listFavoriteHotel)
            adapterFavorite!!.setNewData(listFavoriteHotel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initUi()
            onClick()
        }
    }

    private fun getListFavoriteHotel() {
        val typeToken = object : TypeToken<MutableList<Hotel>>() {}.type
        listFavoriteHotel = try {
            Gson().fromJson(preferenceHelper.listHotelFavorite, typeToken)
        } catch (e: JsonSyntaxException) {
            mutableListOf()
        }
        listFavoriteHotel.sortBy { it.hotelId }
        adapterFavorite!!.setNewData(listFavoriteHotel)
    }

    private fun initUi() {

        adapterFavorite =
            FavoriteHotelAdapter(requireContext(), listFavoriteHotel, clickItem, clickFavorite)

        binding!!.rcvFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterFavorite
        }

        if (preferenceHelper.listHotelFavorite.isNotEmpty()) {
            getListFavoriteHotel()
        }

    }

    private fun onClick() {

    }

    private val clickItem = object : (Int) -> Unit {
        override fun invoke(position: Int) {
            val hotel = listFavoriteHotel[position]
            val hotelJson = Gson().toJson(hotel)
            File(requireActivity().applicationContext.filesDir, globalHelper.HOTEL_FILE_NAME).outputStream()
                .bufferedWriter().use { it.write(hotelJson) }
            val intent = Intent(requireContext(), HotelPreviewActivity::class.java)
            startActivity(intent)
        }
    }

    private val clickFavorite = object : (Int) -> Unit {
        @SuppressLint("NotifyDataSetChanged")
        override fun invoke(position: Int) {
            viewModel.setEventRemoveFavoriteToHome(listFavoriteHotel[position])
            listFavoriteHotel.removeAt(position)
            preferenceHelper.listHotelFavorite = Gson().toJson(listFavoriteHotel)
            adapterFavorite!!.notifyDataSetChanged()
        }
    }
}
package com.example.myBookingHotel.view.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.FragmentHomeBinding
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.view.activity.find.FindLocationActivity
import com.example.myBookingHotel.view.activity.room.HotelPreviewActivity
import com.example.myBookingHotel.view.adapter.room.HotelAdapter
import com.example.myBookingHotel.view.fragment.BaseFragment
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
import java.io.File

class HomeFragment : BaseFragment() {

    private var binding: FragmentHomeBinding? = null
    private var scrollCallback: ScrollCallback? = null
    private var adapterHotel: HotelAdapter? = null

    private var listHotelLocation: MutableList<Hotel> = mutableListOf()

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(scrollCallback: ScrollCallback) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
                setListener(scrollCallback)
            }
    }

    fun setListener(scrollCallback: ScrollCallback) {
        this.scrollCallback = scrollCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModel.getEventRemoveFavoriteToHome.observe(this) {
            listHotelLocation.forEach { hotel ->
                if (hotel.hotelId == it.hotelId) {
                    hotel.isFavorite = false
                }
            }
            adapterHotel!!.setNewData(listHotelLocation)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        } else {
            (binding!!.root.parent as? ViewGroup)?.removeView(binding!!.root)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasInitializedRootView) {
            hasInitializedRootView = true

            initUI()
            onClick()
        }
    }

    private fun initUI() {
        adapterHotel = HotelAdapter(
            requireContext(),
            preferenceHelper = preferenceHelper,
            listHotelLocation,
            clickItem = clickItem,
            clickFavorite = clickFavorite
        )
        binding!!.rcvRoom.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterHotel
        }
        getListHotel()
    }

    private fun getListHotel() {
        showLoading(true)
        listHotelLocation = try {
            Gson().fromJson(preferenceHelper.listHotelLocation, object : TypeToken<MutableList<Hotel>>(){}.type)
        } catch (e: JsonSyntaxException){
            mutableListOf()
        }
        showLoading(false)
        setDataRcv()
    }

    private fun setDataRcv() {
        listHotelLocation.shuffle()
        val newList = mutableListOf<Hotel>()
        for (i in 0..14) {
            if (i < listHotelLocation.size) {
                newList.add(listHotelLocation[i])
            }
        }
        if (preferenceHelper.listHotelFavorite.isNotEmpty()) {
            val listFavorite: MutableList<Hotel> = Gson().fromJson(
                preferenceHelper.listHotelFavorite,
                object : TypeToken<MutableList<Hotel>>() {}.type
            )
            newList.forEach { hotel ->
                listFavorite.forEach { fav ->
                    if (hotel.hotelId == fav.hotelId) {
                        hotel.isFavorite = fav.isFavorite
                    }
                }
            }
        }
        adapterHotel!!.setNewData(newList)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.cvFind.visibility = View.GONE
            binding!!.rcvRoom.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.cvFind.visibility = View.VISIBLE
            binding!!.rcvRoom.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClick() {
        binding!!.cvFind.setOnClickListener {
            startActivity(Intent(requireActivity(), FindLocationActivity::class.java))
        }

        binding!!.srRcv.setOnRefreshListener {
            setDataRcv()
            binding!!.srRcv.isRefreshing = false
        }
    }

    private val clickItem = object : (Int) -> Unit {
        override fun invoke(pos: Int) {
            val hotel = listHotelLocation[pos]
            val hotelJson = Gson().toJson(hotel)
            File(requireActivity().applicationContext.filesDir, globalHelper.HOTEL_FILE_NAME).outputStream()
                .bufferedWriter().use { it.write(hotelJson) }
            val intent = Intent(requireActivity(), HotelPreviewActivity::class.java)
            startActivity(intent)
        }
    }

    private val clickFavorite = object : (Int) -> Unit {
        @SuppressLint("NotifyDataSetChanged")
        override fun invoke(position: Int) {
            if (listHotelLocation[position].isFavorite) {
                viewModel.setEventAddToFavorite(listHotelLocation[position])
            } else {
                viewModel.setEventRemoveFavorite(listHotelLocation[position])
            }
            adapterHotel!!.notifyDataSetChanged()
        }
    }
}
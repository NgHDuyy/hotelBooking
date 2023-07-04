package com.example.myBookingHotel.view.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myBookingHotel.databinding.FragmentRecommendBinding
import com.example.myBookingHotel.model.hotel.Hotel
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

class RecommendFragment : BaseFragment() {

    private var binding: FragmentRecommendBinding? = null
    private var scrollCallback: ScrollCallback? = null

    private var listHotelLocation: MutableList<Hotel> = mutableListOf()

    private var recommendAdapter: HotelAdapter? = null

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(scrollCallback: ScrollCallback) =
            RecommendFragment().apply {
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

        viewModel.getEventAddToFavorite.observe(this) {
            listHotelLocation.forEach { hotel ->
                if (hotel.hotelId == it.hotelId) {
                    hotel.isFavorite = it.isFavorite
                }
            }
            recommendAdapter!!.setNewData(listHotelLocation)
        }

        viewModel.getEventRemoveFavorite.observe(this) {
            listHotelLocation.forEach { hotel ->
                if (hotel.hotelId == it.hotelId) {
                    hotel.isFavorite = false
                }
            }
            recommendAdapter!!.setNewData(listHotelLocation)
        }

        viewModel.getEventRemoveFavoriteToHome.observe(this) {
            listHotelLocation.forEach { hotel ->
                if (hotel.hotelId == it.hotelId) {
                    hotel.isFavorite = false
                }
            }
            recommendAdapter!!.setNewData(listHotelLocation)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = FragmentRecommendBinding.inflate(inflater, container, false)
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
        recommendAdapter = HotelAdapter(
            requireContext(),
            preferenceHelper,
            listHotelLocation,
            clickItem = clickItem,
            clickFavorite = clickFavorite
        )
        binding!!.rcvRecommendHotel.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendAdapter
        }
        getListHotel()
    }


    private fun getListHotel() {
        CoroutineScope(Dispatchers.IO).launch {
            APIHelper().getAllHotel(
                onPre = {
                    showLoading(true)
                },
                onPost = object : StringCallback {
                    override fun execute(string: String?) {
                        if (string != null) {
                            listHotelLocation = Gson().fromJson(
                                string,
                                object : TypeToken<MutableList<Hotel>>() {}.type
                            )
                            setDataRcv()
                            showLoading(false)
                        }
                    }
                }
            )
        }
    }

    private fun setDataRcv() {
        val newList = mutableListOf<Hotel>()
        for (i in listHotelLocation.indices) {
            if (listHotelLocation[i].location?.province!! == binding!!.tvLocation.text) {
                newList.add(listHotelLocation[i])
            }
        }
        newList.sortedByDescending { hotel -> hotel.star }
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
        recommendAdapter!!.setNewData(newList)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.srView.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.srView.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }

    private var listProvince: ArrayList<String> = arrayListOf()

    private fun onClick() {
        binding!!.srView.setOnRefreshListener {
            setDataRcv()
            binding!!.srView.isRefreshing = false
        }
        binding!!.tvLocation.setOnClickListener {
            listProvince = try {
                Gson().fromJson(
                    preferenceHelper.listProvince,
                    object : TypeToken<ArrayList<String>>() {}.type
                )
            } catch (e: JsonSyntaxException) {
                arrayListOf()
            } ?: arrayListOf()
            globalHelper.showChooseLocationDialog(
                requireActivity(),
                requireContext(),
                listProvince,
                onItemClick = object : (String) -> Unit {
                    override fun invoke(strProvince: String) {

                    }
                })
        }
    }

    private val clickItem = object : (Int) -> Unit {
        override fun invoke(pos: Int) {
            val hotel = listHotelLocation[pos]
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
            if (listHotelLocation[position].isFavorite) {
                viewModel.setEventAddToFavorite(listHotelLocation[position])
            } else {
                viewModel.setEventRemoveFavorite(listHotelLocation[position])
            }
            recommendAdapter!!.notifyDataSetChanged()
        }
    }
}
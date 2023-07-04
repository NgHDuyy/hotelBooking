package com.example.myBookingHotel.view.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.myBookingHotel.databinding.FragmentAccountBinding
import com.example.myBookingHotel.view.activity.booking.MyBookingRoomActivity
import com.example.myBookingHotel.view.activity.user.LogInActivity
import com.example.myBookingHotel.view.fragment.BaseFragment
import com.example.myBookingHotel.viewmodel.liveData.MainViewModel
import com.example.myBookingHotel.viewmodel.utils.listener.ScrollCallback

class AccountFragment : BaseFragment() {

    private var binding: FragmentAccountBinding? = null
    private var scrollCallback: ScrollCallback? = null
    private var clickFavoriteListener: (() -> Unit?)? = null

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(
            scrollCallback: ScrollCallback,
            clickFavoriteListener: () -> Unit
        ) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                }
                setListener(
                    scrollCallback = scrollCallback,
                    clickFavoriteListener = clickFavoriteListener
                )
            }
    }

    fun setListener(
        scrollCallback: ScrollCallback,
        clickFavoriteListener: () -> Unit
    ) {
        this.scrollCallback = scrollCallback
        this.clickFavoriteListener = clickFavoriteListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = FragmentAccountBinding.inflate(inflater, container, false)
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
            setOncClick()
        }
    }

    private fun initUI() {
        binding!!.apply {
            tvName.text = preferenceHelper.getInfoUser(1)
            layoutLoginRegister.visibility =
                if (preferenceHelper.statusLogin) View.GONE else View.VISIBLE
            layoutUserInfo.visibility =
                if (preferenceHelper.statusLogin) View.VISIBLE else View.GONE
            tvLanguage.text =
                globalHelper.getLanguageApp(requireContext(), preferenceHelper.languageDevice)
        }
    }

    private var isShowDialogLanguage = false

    private fun setOncClick() {
        binding!!.tvLogOut.setOnClickListener {
            preferenceHelper.statusLogin = false
            startActivity(Intent(requireActivity(), LogInActivity::class.java))
            requireActivity().finish()
        }

        binding!!.tvMyFavoriteHotel.setOnClickListener {
            clickFavoriteListener?.invoke()
        }

        binding!!.layoutLanguage.setOnClickListener {
            if (!isShowDialogLanguage) {
                isShowDialogLanguage = true
                globalHelper.showDialogLanguage(
                    activity, preferenceHelper.languageDevice, languageListener = {
                        isShowDialogLanguage = false
                        if (it == null) return@showDialogLanguage
                        if (preferenceHelper.languageDevice != it) {
                            setLanguage(it)
                        }
                    }
                )
            }
        }

        binding!!.tvMyBooking.setOnClickListener {
            startActivity(Intent(requireActivity(), MyBookingRoomActivity::class.java))
        }
    }

    private fun setLanguage(language: String) {
        if (!isAdded) return
        preferenceHelper.languageDevice = language
        binding!!.tvLanguage.text = globalHelper.getLanguageApp(requireContext(), language)
        viewModel.setEventOnRestart(MainViewModel.ON_RESTART_APP)
    }
}
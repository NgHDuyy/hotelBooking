package com.example.myBookingHotel.view.activity.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.myBookingHotel.databinding.ActivityLogInBinding
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.view.activity.main.MainActivity
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper
import com.example.myBookingHotel.viewmodel.utils.helper.GlobalHelper
import com.google.gson.Gson
import java.util.regex.Pattern


class LogInActivity : BaseActivity() {

    private var binding: ActivityLogInBinding? = null
    private var isSettingFragment: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        isSettingFragment = intent.extras?.getBoolean("isSettingFragment", false)
        setContentView(binding!!.root)
        initUI()
        onClick()
    }

    private fun initUI() {
        isSettingFragment?.let {
            binding?.icBack?.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding?.apply {
            tvLoginSuccess.visibility = View.GONE
            tvLoginFail.visibility = View.GONE
        }
    }

    private fun onClick() {
        binding?.let {
            it.tvRegister.setOnClickListener { _ ->
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            it.btnLogIn.setOnClickListener { _ ->
                GlobalHelper().closeKeyboard(this@LogInActivity)
                showLoading(true)
                if (validatePhone()) {
                    it.tvIncorrectPhone.visibility = View.GONE
                    if (validatePassword()) {
                        it.tvIncorrectPhone.visibility = View.GONE
                        val phone = binding!!.etSdt.text.toString().trim()
                        val password = binding!!.etPassword.text.toString().trim()
                        APIHelper().login(phone, password, onSuccess = { userId ->
                            if (!userId.isNullOrEmpty()) {
                                APIHelper().getUser(userId, onSuccess = { user ->
                                    binding!!.tvLoginFail.visibility = View.GONE
                                    binding!!.tvLoginSuccess.visibility = View.VISIBLE
                                    preferenceHelper.profileUser = Gson().toJson(user)
                                    preferenceHelper.statusLogin = true
                                    showLoading(false)
                                    Handler().postDelayed({
                                        val intent =
                                            Intent(this@LogInActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }, 2000)
                                })
                            } else {
                                binding!!.tvLoginSuccess.visibility = View.GONE
                                binding!!.tvLoginFail.visibility = View.VISIBLE
                                showLoading(false)
                            }
                        })
                    } else {
                        it.tvIncorrectPassword.visibility = View.VISIBLE
                        showLoading(false)
                    }
                } else {
                    it.tvIncorrectPhone.visibility = View.VISIBLE
                    showLoading(false)
                }
            }
            it.icBack.setOnClickListener { _ ->
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.btnLogIn.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.btnLogIn.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }

    private val PHONE_NUMBER_PATTERN = "^0\\d{9}$"

    private fun validatePhone(): Boolean {
        return Pattern.compile(PHONE_NUMBER_PATTERN).matcher(binding!!.etSdt.text.toString().trim())
            .matches() && binding!!.etSdt.text.toString().trim().isNotEmpty()
    }

    private fun validatePassword(): Boolean {
        return binding!!.etPassword.text.toString().trim().length > 5
    }

}
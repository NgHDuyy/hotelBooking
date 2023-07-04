package com.example.myBookingHotel.view.activity.user

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.View
import com.example.myBookingHotel.databinding.ActivityRegisterBinding
import com.example.myBookingHotel.view.activity.BaseActivity
import com.example.myBookingHotel.viewmodel.utils.api.APIHelper

class RegisterActivity : BaseActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initUI()
        onClick()
    }

    private fun initUI() {
        binding?.apply {
            tvInvalidateName.visibility = View.GONE
            tvInvalidatePhone.visibility = View.GONE
            tvInvalidateEmail.visibility = View.GONE
            tvInvalidatePassword.visibility = View.GONE
            tvInvalidateRePassword.visibility = View.GONE
            tvRegisterSuccess.visibility = View.GONE
            tvRegisterFail.visibility = View.GONE
        }
    }

    private fun onClick() {
        binding?.let {
            it.btnRegister.setOnClickListener { _ ->
                showLoading(true)
                globalHelper.closeKeyboard(this@RegisterActivity)
                if (checkValidateName()
                    && checkValidatePhone()
                    && checkValidateEmail()
                    && checkValidatePassword()
                    && checkValidateRePassword()
                ) {
                    APIHelper().createId { id ->
                        if (!id.isNullOrEmpty()) {
                            val name = binding!!.etName.text.toString().trim()
                            val email = binding!!.etEmail.text.toString().trim()
                            val phone = binding!!.etPhoneNumber.text.toString().trim()
                            val password = binding!!.etPassword.text.toString().trim()
                            APIHelper().register(
                                id = id,
                                name = name,
                                email = email,
                                phone = phone,
                                password = password,
                                onSuccess = { success ->
                                    if (success != null) {
                                        showLoading(false)
                                        if (success) {
                                            binding!!.tvRegisterSuccess.visibility = View.VISIBLE
                                            binding!!.tvRegisterFail.visibility = View.GONE
                                            Handler().postDelayed({
                                                finish()
                                            }, 2000)
                                        } else {
                                            binding!!.tvRegisterFail.visibility = View.VISIBLE
                                            binding!!.tvRegisterSuccess.visibility = View.GONE
                                        }
                                    }
                                })
                        }
                    }
                } else {
                    if (!checkValidateName()) {
                        binding!!.tvInvalidateName.visibility = View.VISIBLE
                    }
                    if (!checkValidatePhone()) {
                        binding!!.tvInvalidatePhone.visibility = View.VISIBLE
                    }
                    if (!checkValidateEmail()) {
                        binding!!.tvInvalidateEmail.visibility = View.VISIBLE
                    }
                    if (!checkValidatePassword()) {
                        binding!!.tvInvalidatePassword.visibility = View.VISIBLE
                    }
                    if (!checkValidateRePassword()) {
                        binding!!.tvInvalidateRePassword.visibility = View.VISIBLE
                    }
                    showLoading(false)
                }
            }
            it.icBack.setOnClickListener { finish() }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding!!.btnRegister.visibility = View.GONE
            binding!!.pbUpdateData.visibility = View.VISIBLE
        } else {
            binding!!.btnRegister.visibility = View.VISIBLE
            binding!!.pbUpdateData.visibility = View.GONE
        }
    }

    private fun checkValidateName(): Boolean {
        return binding!!.etName.text.toString().trim().isNotEmpty()
    }

    private fun checkValidatePhone(): Boolean {
        val phone = binding!!.etPhoneNumber.text.toString().trim()
        return Patterns.PHONE.matcher(phone).matches()
    }

    private fun checkValidateEmail(): Boolean {
        val email = binding!!.etEmail.text.toString().trim()
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkValidatePassword(): Boolean {
        return binding!!.etPassword.text.toString().trim().length > 5
    }

    private fun checkValidateRePassword(): Boolean {
        return binding!!.etConfirmPassword.text.toString()
            .trim() == binding!!.etPassword.text.toString().trim()
    }
}
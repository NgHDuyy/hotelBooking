package com.example.myBookingHotel.viewmodel.utils.api

import android.util.Log
import com.example.myBookingHotel.model.booking.Bill
import com.example.myBookingHotel.model.hotel.Hotel
import com.example.myBookingHotel.model.user.User
import com.example.myBookingHotel.viewmodel.utils.helper.GlobalHelper
import com.example.myBookingHotel.viewmodel.utils.listener.StringCallback
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class APIHelper(private val BASE_URL: String = "http://192.168.15.47:8080/") {

    private var bookingApi: API? = null

    private val api: API
        get() {
            if (bookingApi == null) {
                bookingApi = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(API::class.java)
            }
            return bookingApi!!
        }

    fun getAllHotel(onPre: () -> Unit, onPost: StringCallback) {
        onPre.invoke()
        api.getAllHotel().enqueue(object : Callback<List<Hotel>?> {
            override fun onResponse(call: Call<List<Hotel>?>, response: Response<List<Hotel>?>) {
                if (response.body() != null) {
                    onPost.execute(Gson().toJson(response.body()))
                }
            }

            override fun onFailure(call: Call<List<Hotel>?>, t: Throwable) {
                Log.d(GlobalHelper().DEBUG, "onFailure: ${t.message}")
            }
        })
    }

    fun login(phone: String, password: String, onSuccess: (String?) -> Unit) {
        api.login(phone, password).enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.isSuccessful) {
                    if (!response.body().isNullOrEmpty()) {
                        onSuccess.invoke(response.body())
                    } else {
                        onSuccess.invoke("")
                    }
                } else {
                    onSuccess.invoke("")
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("DEBUG", "onFailure: $t")
            }

        })
    }

    fun getUser(userId: String, onSuccess: (User?) -> Unit) {
        api.getUser(userId).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        onSuccess.invoke(response.body())
                    } else {
                        Log.d("DEBUG", "onFail")
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Log.d("DEBUG", "onFailure: ${t.message}")
            }

        })
    }

    fun register(
        id: String,
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: (Boolean?) -> Unit
    ) {
        api.register(id, name, email, phone, password).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess.invoke(response.body())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                onSuccess.invoke(false)
            }

        })
    }

    fun createId(onSuccess: (String?) -> Unit) {
        api.getAllUser().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listUser = response.body()
                    if (listUser != null) {
                        val lastID = listUser[listUser.size - 1].userId
                        if (lastID != null) {
                            val id = "kh" + (lastID.replace("kh", "").toInt() + 1)
                            onSuccess.invoke(id)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                onSuccess.invoke("")
            }
        })
    }

    fun createBill(bill: Bill, onSuccess: () -> Unit) {
        api.createBill(bill).enqueue(object : Callback<Bill> {
            override fun onResponse(call: Call<Bill>, response: Response<Bill>) {
                if (response.isSuccessful && response.body() != null) {
                    val resBill = response.body()
                    if (resBill != null && resBill == bill) {
                        onSuccess.invoke()
                    }
                }
            }

            override fun onFailure(call: Call<Bill>, t: Throwable) {
                Log.d("TAGa", "onFailure: ${t.message}")
            }

        })
    }

    fun getBookingByCustomerId(customerId: String, onPre: () -> Unit, onSuccess: (String) -> Unit) {
        onPre.invoke()
        api.getBookingByCustomerId(customerId).enqueue(object : Callback<ArrayList<Bill>> {
            override fun onResponse(
                call: Call<ArrayList<Bill>>,
                response: Response<ArrayList<Bill>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess.invoke(Gson().toJson(response.body()))
                }
            }

            override fun onFailure(call: Call<ArrayList<Bill>>, t: Throwable) {
                onSuccess.invoke("")
            }
        })
    }

    fun getHotelById(hotelId: String, onPre: () -> Unit, onSuccess: (String?) -> Unit) {
        onPre.invoke()
        api.getHotelById(hotelId).enqueue(object : Callback<Hotel> {
            override fun onResponse(call: Call<Hotel>, response: Response<Hotel>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess.invoke(Gson().toJson(response.body()))
                }
            }

            override fun onFailure(call: Call<Hotel>, t: Throwable) {
                Log.d("DEBUG", "onFailure: ${t.message}")
            }
        })
    }

    fun deleteBooking(id: String, onPre: () -> Unit, onSuccess: (String) -> Unit){
        onPre.invoke()
        api.deleteBooking(id).enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    onSuccess.invoke(response.body()!!)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("DEBUG", "onFailure: ${t.message}")
            }

        })
    }

    interface API {
        @GET("hotel/list")
        fun getAllHotel(): Call<List<Hotel>>

        @GET("customer/login")
        fun login(
            @Query("phone") phone: String,
            @Query("password") password: String
        ): Call<String?>

        @GET("customer")
        fun getUser(
            @Query("id") userId: String
        ): Call<User?>

        @POST("customer/register")
        @FormUrlEncoded
        fun register(
            @Field("id") userId: String,
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("phone") phone: String,
            @Field("password") password: String
        ): Call<Boolean>

        @GET("customer/list")
        fun getAllUser(): Call<List<User>>

        @POST
        @FormUrlEncoded
        fun createBill(
            @Body bill: Bill
        ): Call<Bill>


        @GET("booking/bookingByCustomerId")
        fun getBookingByCustomerId(
            @Query("customer_id") customerId: String
        ): Call<ArrayList<Bill>>

        @GET("hotel")
        fun getHotelById(
            @Query("id") hotelId: String
        ): Call<Hotel>

        @DELETE("booking")
        fun deleteBooking(
            @Query("id") id: String
        ): Call<String>
    }
}
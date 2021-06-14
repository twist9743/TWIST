package com.example.lunin_ia_191_352.ui.Lab8

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.R
import com.google.android.gms.common.util.Base64Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class Lab8Fragment : Fragment() {

    private lateinit var lab8ViewModel: Lab8ViewModel
    private lateinit var login : EditText
    private lateinit var passwd : EditText
    private lateinit var jwt_token : TextView
    private lateinit var image : ImageView
    private lateinit var login_btn : Button
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    var url = "https://kotlin-jwt.herokuapp.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab8ViewModel =
            ViewModelProvider(this).get(Lab8ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab8, container, false)
        login = root.findViewById(R.id.login)
        passwd = root.findViewById(R.id.passwd)
        jwt_token = root.findViewById(R.id.JWT)
        image = root.findViewById(R.id.image)
        login_btn = root.findViewById(R.id.jwtlogin)

        login_btn.setOnClickListener{
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                param(FirebaseAnalytics.Param.ITEM_NAME, "Button")
            }

            val data = hashMapOf<String,String>()
            data["username"] = login.text.toString()
            data["password"] = passwd.text.toString()
            val json = Gson().toJson(data)

            val client = OkHttpClient().newBuilder()
                .build()
            val mediaType: MediaType? = "application/json".toMediaTypeOrNull()
            var body: RequestBody = json.toRequestBody(mediaType)
            var request: Request = Request.Builder()
                .url("$url/auth")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle this
                    Log.d("Failure",e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    // Handle this
                    GlobalScope.launch(Dispatchers.Main){
                        val res = Gson().fromJson(response.body!!.string(), Map::class.java)
                        jwt_token.text =res["access_token"].toString()

                        if (res["access_token"]!=null) {
                            request = Request.Builder().url("$url/protected").method("GET", null)
                                .addHeader("Content-Type", "application/json")
                                .addHeader("Authorization", "JWT "+res["access_token"]).build()
                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    // Handle this
                                    Log.d("Failure",e.toString())
                                }
                                override fun onResponse(call: Call, response: Response) {
                                    val res = Gson().fromJson(response.body!!.string(), Map::class.java)


                                    Log.d("image",res.toString())
                                    val imageBytes = Base64.decode(res["img"].toString(), Base64.DEFAULT)

                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    GlobalScope.launch(Dispatchers.Main) {
                                        image.setImageBitmap(decodedImage)
                                    }
                                }
                            })
                        }
                    }
                }
            })




        }




        return root
    }
}
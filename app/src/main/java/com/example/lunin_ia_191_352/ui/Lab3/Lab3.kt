package com.example.lunin_ia_191_352.ui.lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.MainActivity
import com.example.lunin_ia_191_352.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.ui.VKWebViewAuthActivity


class Lab3Fragment : Fragment() {

    private lateinit var lab3ViewModel: Lab3ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab3ViewModel =
            ViewModelProvider(this).get(Lab3ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab4, container, false)
        val vkBtn = root.findViewById<Button>(R.id.vkbtn)
        val vkToken = root.findViewById<TextView>(R.id.textView3)
        vkBtn.setOnClickListener {
            VK.login(requireActivity(), arrayListOf(VKScope.WALL, VKScope.PHOTOS))
            if (VK.isLoggedIn()) {
                val token = VKAccessToken.restore(
                    activity?.getSharedPreferences(
                        "com.vkontakte.android_pref_name",
                        Context.MODE_PRIVATE))
                vkToken.text = token?.accessToken
            }
        }
        return root
    }


    companion object {
        fun startFrom(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}





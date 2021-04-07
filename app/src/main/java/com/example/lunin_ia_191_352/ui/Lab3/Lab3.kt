package com.example.lunin_ia_191_352.ui.lab3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope


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
        val vkbtn = root.findViewById<Button>(R.id.vkbtn)
        vkbtn.setOnClickListener{
            VK.login(requireActivity(), arrayListOf(VKScope.WALL, VKScope.PHOTOS))
        }


        return root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}





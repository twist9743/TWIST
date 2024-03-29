package com.example.lunin_ia_191_352.ui.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.R
import com.vk.api.sdk.VK
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
        val vkBtn = root.findViewById<Button>(R.id.vkbtn)
        val vkToken = root.findViewById<TextView>(R.id.textView3)
        vkBtn.setOnClickListener {
            VK.login(requireActivity(), arrayListOf(VKScope.FRIENDS, VKScope.PHOTOS))

        }
        return root
    }



}





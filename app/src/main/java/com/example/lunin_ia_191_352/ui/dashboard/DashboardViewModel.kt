package com.example.lunin_ia_191_352.ui.dashboard

import android.net.Uri
import android.widget.MediaController
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lunin_ia_191_352.R

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"

    }
    val text: LiveData<String> = _text


}
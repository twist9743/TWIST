package com.example.lunin_ia_191_352.ui.dashboard

import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.R
import kotlinx.coroutines.*
import android.widget.SeekBar.OnSeekBarChangeListener


class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)


        val videoView = root.findViewById<VideoView>(R.id.CustomVideo)


        val offlineUri = Uri.parse("android.resource://com.example.lunin_ia_191_352/2131820544")

        val videoPos = root.findViewById<SeekBar>(R.id.videoPos)
        videoView?.setVideoURI(offlineUri)
        videoView?.requestFocus()
        val stop_play_btn = root.findViewById<Button>(R.id.stop_play)


        videoPos.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean){

            }


            override fun onStartTrackingTouch(seekBar: SeekBar){

            }

            override fun onStopTrackingTouch(seekBar: SeekBar){
                videoView.seekTo(videoPos.progress*1000+1000)
            }
        })



        videoView.setOnPreparedListener {
            videoPos.max = videoView.duration / 1000
            stop_play_btn.setOnClickListener {

                if (!videoView.isPlaying) {
                    stop_play_btn.setBackgroundResource(R.drawable.pause)
                    videoView.start()
                    GlobalScope.launch {
                        while (videoView.isPlaying) {
                            videoPos.progress = videoView.currentPosition / 1000
                        }
                    }
                } else {
                    stop_play_btn.setBackgroundResource(R.drawable.play)
                    videoView.pause()
                }
            }
        }
        videoView.setOnCompletionListener(OnCompletionListener {
            videoView.seekTo(1)
            stop_play_btn.setBackgroundResource(R.drawable.play)
        })





        return root

    }

}

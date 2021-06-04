package com.example.lunin_ia_191_352.ui.Lab7

import android.net.Uri
import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunin_ia_191_352.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI


class Lab7Fragment : Fragment() {

    private lateinit var lab7ViewModel: Lab7ViewModel
    private lateinit var msg_place : ScrollView
    private lateinit var user_request: EditText
    private lateinit var send_message: Button
    private lateinit var btn_connect: Button
    private lateinit var wss_client : WebSocketClient
    private var server_msg : String = ""
    lateinit var messagesRecyclerView: RecyclerView
    val socket_link = URI("wss://chatbot-kotlin.herokuapp.com/")
    var messagesAll = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab7ViewModel =
            ViewModelProvider(this).get(Lab7ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab7, container, false)
        send_message = root.findViewById(R.id.sendmessage)
        user_request = root.findViewById(R.id.user_text)


        messagesRecyclerView = root.findViewById(R.id.albums)

        messagesRecyclerView.layoutManager = LinearLayoutManager(context)
        messagesRecyclerView.adapter = Messages(messagesAll)

        btn_connect = root.findViewById(R.id.connect)
        btn_connect.setOnClickListener{
            createWebSocketClient(socket_link)
            wss_client.connect()
        }
        send_message.setOnClickListener{
            val message = user_request.text.toString()
            messagesAll.add(Message("user", message))
            wss_client.send(message)
            user_request.text.clear()


        }

        return root
    }
    fun createWebSocketClient(uri:URI){
        wss_client = object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d("Socket","opened")

            }

            override fun onMessage(message: String?) {
                if (message != null) {
                    val jsonmessage = JSONObject(message)
                    GlobalScope.launch {
                        Log.d("Socket",message)
                        Log.d("body", jsonmessage.get("body") as String)
                        messagesAll.add(Message("Bot", jsonmessage.get("body") as String))
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                wss_client.close()
            }

            override fun onError(ex: Exception?) {
                Log.d("Socket", ex.toString())
            }

        }
    }
    
    data class Message(val who:String, val text:String)
        
    
    class Messages(private val messagesList: MutableList<Message>) :
        RecyclerView.Adapter<Messages.MsgViewHolder>() {
        inner class MsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var text: TextView = itemView.findViewById(R.id.user_msg_model)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.user_message, parent, false)
            return MsgViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
            val item = messagesList[position]
            holder.apply {
                text.text = item.text


            }
        }

        override fun getItemCount() = messagesList.size

        fun updateData(newMsgs: MutableList<Message>){
            this.apply {
                messagesList.clear()
                messagesList.addAll(newMsgs)
            }
            notifyDataSetChanged()
        }
    }



}

package com.example.lunin_ia_191_352.ui.Lab7

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunin_ia_191_352.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


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


        messagesRecyclerView = root.findViewById(R.id.messages_recycler_view)

        messagesRecyclerView.layoutManager = LinearLayoutManager(context)
        messagesRecyclerView.adapter = Messages(messagesAll)

        btn_connect = root.findViewById(R.id.connect)
        btn_connect.setOnClickListener{
            createWebSocketClient(socket_link)
            wss_client.connect()
        }
        send_message.setOnClickListener{
            val message = user_request.text.toString()
            val date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            messagesAll.add(Message("user", message,date))
            wss_client.send(message)
            user_request.text.clear()
            (messagesRecyclerView.adapter as Messages).notifyDataSetChanged()


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
                    GlobalScope.launch(Dispatchers.Main) {
                        Log.d("Socket",message)
                        Log.d("body", jsonmessage.get("body") as String)
                        val date = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        messagesAll.add(Message("Bot", jsonmessage.get("body") as String,date))
                        (messagesRecyclerView.adapter as Messages).notifyDataSetChanged()
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
    
    data class Message(val who:String, val text:String, val date: String)
        
    




}
class Messages(private val messagesList: MutableList<Lab7Fragment.Message>) :
    RecyclerView.Adapter<Messages.MsgViewHolder>() {
    inner class MsgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var bot_text: TextView = itemView.findViewById(R.id.bot_msg_model)
        var user_text: TextView = itemView.findViewById(R.id.user_msg_model)
        var bot_layout :LinearLayout = itemView.findViewById(R.id.bot_layout)
        var user_layout :LinearLayout = itemView.findViewById(R.id.user_layout)
        var bot_spacer : Space = itemView.findViewById(R.id.bot_spacer)
        var user_spacer : Space = itemView.findViewById(R.id.user_spacer)
        var bot_date : TextView = itemView.findViewById(R.id.bot_date)
        var user_date : TextView = itemView.findViewById(R.id.user_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_message, parent, false)
        return MsgViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val item = messagesList[position]
        holder.apply {
            if (item.who=="user"){
                user_text.text=item.text
                bot_layout.visibility = View.GONE
                user_layout.visibility = View.VISIBLE
                user_spacer.visibility = View.VISIBLE
                bot_spacer.visibility=View.GONE
                bot_date.visibility = View.GONE
                user_date.visibility = View.VISIBLE
                user_date.text=item.date
            }
            else{
                bot_text.text=item.text
                user_layout.visibility = View.GONE
                bot_layout.visibility = View.VISIBLE
                bot_spacer.visibility = View.VISIBLE
                user_spacer.visibility=View.GONE
                user_date.visibility = View.GONE
                bot_date.visibility = View.VISIBLE
                bot_date.text=item.date
            }
        }

    }

    override fun getItemCount() = messagesList.size


}
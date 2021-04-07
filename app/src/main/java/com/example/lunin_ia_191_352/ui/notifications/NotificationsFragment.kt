package com.example.lunin_ia_191_352.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val btn = root.findViewById<Button>(R.id.button)
        var text_html = root.findViewById<TextView>(R.id.editTextTextMultiLine)
        var curs_backsa = root.findViewById<TextView>(R.id.textView2)

        btn.setOnClickListener{
            GlobalScope.async {
                val doc: Document = Jsoup.connect("https://www.cbr.ru/").get()
                val parse_test = doc.text()
                val mainHeaderElements: Elements = doc.select("#content > div > div > div > div.home-main > div.home-main_aside > div > div.main-indicator_rates > div > div:nth-child(2) > div:nth-child(2)")
                val test : String = mainHeaderElements.toString()
                val oldValue = "<div class=\"col-md-2 col-xs-9 _right mono-num\">"
                val newValue = ""
                val test2 = test.replace(oldValue,newValue,ignoreCase=true)
                val oldValue2 = "</div>"
                val final = test2.replace(oldValue2,newValue,ignoreCase=true)
                while (curs_backsa.toString().isNotEmpty() and text_html.toString().isNotEmpty()){
                    curs_backsa.text = final
                    text_html.text = parse_test
                }

            }
        }





        return root
    }



}
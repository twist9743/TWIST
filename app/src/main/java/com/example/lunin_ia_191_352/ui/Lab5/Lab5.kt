package com.example.lunin_ia_191_352.ui.Lab5

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunin_ia_191_352.R
import com.squareup.picasso.Picasso
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.friends.FriendsService
import com.vk.sdk.api.friends.dto.FriendsGetFieldsResponse
import com.vk.sdk.api.users.dto.UsersFields


class Lab5Fragment : Fragment() {

    private lateinit var lab5ViewModel: Lab5ViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab5ViewModel =
            ViewModelProvider(this).get(Lab5ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab5, container, false)
        val btn = root.findViewById<Button>(R.id.lab5btn)
        btn.setOnClickListener {
            val fields = listOf(UsersFields.PHOTO_200)
            VK.execute(FriendsService().friendsGet(fields = fields), object :
                VKApiCallback<FriendsGetFieldsResponse> {
                override fun success(result: FriendsGetFieldsResponse) {
                    val friends = result.items
                    val vkUsers = friends.map { friend ->
                        VKUser(
                            id = friend.id ?: 0,
                            firstName = friend.firstName ?: "",
                            lastName = friend.lastName ?: "",
                            photo = friend.photo200 ?: "",
                            deactivated = friend.deactivated != null
                        )
                    }
                    showFriends(vkUsers,root)
                }

                override fun fail(error: Exception) {
                    Log.e(TAG, error.toString())
                }

            })

        }
        return root
    }
    private fun showFriends(friends: List<VKUser>,root:View) {
        val recyclerView = root.findViewById<RecyclerView>(R.id.VkFriends)
        recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(),GridLayoutManager.VERTICAL)

        val adapter = FriendsAdapter()
        adapter.setData(friends)

        recyclerView.adapter = adapter
    }
    inner class FriendsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val friends: MutableList<VKUser> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = UserHolder(parent.context)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as UserHolder).bind(friends[position])
        }

        fun setData(friends: List<VKUser>) {
            this.friends.clear()
            this.friends.addAll(friends)
            notifyDataSetChanged()
        }

        override fun getItemCount() = friends.size
    }
    inner class UserHolder(context: Context?): RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_user, null)) {
        private val avatarIV: ImageView = itemView.findViewById(R.id.avatarIV)
        private val nameTV: TextView = itemView.findViewById(R.id.nameTV)

        fun bind(user: VKUser) {
            nameTV.text = "${user.firstName} ${user.lastName} "
            //avatarIV.setImageResource(user.photo.)
            avatarIV.setImageResource(R.drawable.user_placeholder)
            Picasso.get()
                .load(user.photo)
                .into(avatarIV)

        }
    }
}


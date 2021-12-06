package com.pdinc.chitchat.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdinc.chitchat.Activities.*
import com.pdinc.chitchat.Modals.EmptyViewHolder
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.Modals.UserViewHolder
import com.pdinc.chitchat.R


private const val TAG = "PeopleFragment"
private const val DELETED_VIEW_TYPE=1
private const val NORMAL_VIEW_TPE=2
class PeopleFragment : Fragment() {
    private lateinit var mAdapter: FirestorePagingAdapter<User,RecyclerView.ViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var cc:RecyclerView;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val r=layoutInflater.inflate(R.layout.fragment_blank, container, false)
        return r
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cc= view.findViewById(R.id.chatrv)
        setUpAdapter()
        cc.adapter=mAdapter
        cc.layoutManager=LinearLayoutManager(requireContext())
    }
    private fun setUpAdapter() {
        val config = PagedList.Config.Builder()
            //No. of pages want to get
            .setPrefetchDistance(2)
            //No. of contents want to fetch
            .setPageSize(10)
            //.setEnablePlaceholders(false)
            .build()
        val database= firestore.collection("users").orderBy("name", Query.Direction.ASCENDING)
        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database, config, User::class.java)
            .build()
        Log.d(TAG, "setupAdapter: ")
        mAdapter =object :FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                Log.d("MAdapter", "Work Done")
                val v = layoutInflater.inflate(R.layout.list_item, parent, false)
                val inflater = layoutInflater
                return when (viewType) {
                    NORMAL_VIEW_TPE -> UserViewHolder(inflater.inflate(R.layout.list_item, parent, false))
                    else -> EmptyViewHolder(inflater.inflate(R.layout.empty_view, parent, false))
                }
            }
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if (holder is UserViewHolder) {
                    if (auth.uid == model.uid) {
                        currentList?.snapshot()?.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    holder.bind(user = model) { name: String, photo: String, id: String ->
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(UID, id)
                        intent.putExtra(NAME, name)
                        intent.putExtra(IMAGE, photo)
                        startActivity(intent)
                    }
                } else {
                }
            }
            override fun getItemViewType(position: Int): Int {
                val item = getItem(position)?.toObject(User::class.java)
                Log.d(TAG,"I'm HERE")
                return if (auth.uid == item!!.uid) {
                    DELETED_VIEW_TYPE
                } else {
                    NORMAL_VIEW_TPE
                }
            }
        }
        Log.d(TAG,"Done")
        }
}
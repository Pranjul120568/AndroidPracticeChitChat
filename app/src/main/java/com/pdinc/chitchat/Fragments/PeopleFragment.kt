package com.pdinc.chitchat.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdinc.chitchat.Activities.ChatActivity
import com.pdinc.chitchat.Activities.IMAGE
import com.pdinc.chitchat.Activities.NAME
import com.pdinc.chitchat.Activities.UID
import com.pdinc.chitchat.Modals.EmptyViewHolder
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.Modals.UserVIewHolder
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.FragmentBlankBinding
import java.lang.Exception
private const val DELETED_VIEW_TPE=1
private const val NORMAL_VIEW_TPE=2

lateinit var mAdapter: FirestorePagingAdapter<User,RecyclerView.ViewHolder>
val auth by lazy {
    FirebaseAuth.getInstance()
}
val database by lazy {
    FirebaseFirestore.getInstance().collection("users").orderBy("name", Query.Direction.ASCENDING)
}
lateinit var binding: FragmentBlankBinding
class PeopleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setUpAdapter()
//        return super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_blank,container,false)
    }
    private fun setUpAdapter() {
        val config =PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            //No. of contents want to fetch
            .setPageSize(10)
            //No. of pages want to get
            .setPrefetchDistance(2).build()
        val options=FirestorePagingOptions.Builder<User>().setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database,config,User::class.java).build()
        mAdapter=object :FirestorePagingAdapter<User,RecyclerView.ViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when(viewType){
                    NORMAL_VIEW_TPE -> UserVIewHolder(layoutInflater.inflate(R.layout.list_item,parent,false))
                    else -> EmptyViewHolder(layoutInflater.inflate(R.layout.empty_view,parent,false))
                }}
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if (holder is UserVIewHolder) {
                    holder.bind(user = model){ name:String, photo:String, id:String ->
                    val intent=Intent(requireContext(),ChatActivity::class.java)
                        intent.putExtra(UID,id)
                        intent.putExtra(NAME,name)
                        intent.putExtra(IMAGE,photo)
                        startActivity(intent)
                    }
                }else{
                }
            }
            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when(state){
                    LoadingState.LOADING_INITIAL -> TODO()
                    LoadingState.LOADING_MORE -> TODO()
                    LoadingState.LOADED -> TODO()
                    LoadingState.FINISHED -> TODO()
                    LoadingState.ERROR -> TODO()
                //This is used to handle error cases
                }
            }
            override fun onError(e: Exception) {
                super.onError(e)
            }
            override fun getItemViewType(position: Int): Int {
                val item=getItem(position)?.toObject(User::class.java)
                return if(auth.uid==item!!.uid){
                    DELETED_VIEW_TPE
                }else{
                    NORMAL_VIEW_TPE
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        val recyclerView= binding.chatrv
      recyclerView.apply {
          layoutManager=LinearLayoutManager(requireContext())
          adapter= mAdapter
      }
    }

}
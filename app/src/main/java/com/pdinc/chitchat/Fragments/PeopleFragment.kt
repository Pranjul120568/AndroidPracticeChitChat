package com.pdinc.chitchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.Modals.UserVIewHolder
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.FragmentBlankBinding
import java.lang.Exception

lateinit var mAdapter: FirestorePagingAdapter<User,UserVIewHolder>
val auth by lazy {
    FirebaseAuth.getInstance()
}
val database by lazy {
    FirebaseFirestore.getInstance().collection("users").orderBy("name", Query.Direction.DESCENDING)
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
        mAdapter=object :FirestorePagingAdapter<User,UserVIewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVIewHolder {
                val view=layoutInflater.inflate(R.layout.list_item,parent,false)
                return UserVIewHolder(view)
            }

            override fun onBindViewHolder(holder: UserVIewHolder, position: Int, model: User) =holder.bind(user = model)
            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when(state){
                    LoadingState.LOADING_INITIAL -> TODO()
                    LoadingState.LOADING_MORE -> TODO()
                    LoadingState.LOADED -> TODO()
                    LoadingState.FINISHED -> TODO()
                    LoadingState.ERROR -> TODO()
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
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
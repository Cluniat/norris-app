package com.example.norris_app.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.norris_app.HomeActivity
import com.example.norris_app.R
import com.example.norris_app.adapter.FavouriteListAdapter
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.databinding.FragmentFavouriteListBinding
import com.example.norris_app.model.User
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.UserViewModelFactory
import es.dmoral.toasty.Toasty


class FavouriteListFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteListBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application

        val database = NorrisDatabase.getInstance(application).userDao

        val userViewModelFactory = UserViewModelFactory(database, application)

        val args = FavouriteListFragmentArgs.fromBundle(arguments!!)

        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        currentUser = args.user

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_list, container, false)

        binding.lifecycleOwner = this

        val adapter = FavouriteListAdapter()
        binding.list.adapter = adapter

        adapter.data = parseFavourites(currentUser)!!

        //trigger list actions
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    removeFavourite(adapter.data[viewHolder.adapterPosition], adapter)
                }
            }
        // attaching the touch helper to recycler view
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.list)

        return binding.root
    }

    /**
     * Transform user favourites into a list of quotes
     */
    private fun parseFavourites(user: User): List<String>? {
        return if (user.favourites != null && user.favourites != "") {
            val split = user.favourites!!.split('~')
            split.filter { it != "" }
        } else {
            null
        }

    }

    /**
     * Remove quote from user's favourite list
     */
    private fun removeFavourite(quote: String, adapter: FavouriteListAdapter): User? {
        val userUpdated: User? = null
        userViewModel.removeFavourite(currentUser.id, quote)
        { isRemoved: Boolean, user: User? ->
            requireNotNull(this.activity).runOnUiThread {
                if (isRemoved) {
                    if (user!!.favourites == null || user.favourites == "") {
                        val homeActivityIntent = Intent(activity!!, HomeActivity::class.java)
                        startActivity(homeActivityIntent)
                    } else {
                        adapter.data = parseFavourites(user)!!
                    }
                } else {
                    Toasty.error(
                        activity!!.applicationContext,
                        "Sorry ! Chuck Norris doesn't want that."
                    )
                }

            }
        }
        return userUpdated
    }
}

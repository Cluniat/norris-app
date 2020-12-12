package com.example.norris_app.fragment

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.norris_app.R
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.databinding.FragmentQuoteBinding
import com.example.norris_app.model.User
import com.example.norris_app.viewmodel.ApiQuoteViewModel
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.UserViewModelFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_quote.*


class QuoteFragment : Fragment() {

    private lateinit var apiQuoteViewModel: ApiQuoteViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: FragmentQuoteBinding
    private lateinit var application: Application
    private var currentUserId: Long = 0
    private var favourite: Drawable? = null
    private var favouriteBorder: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        application = requireNotNull(this.activity).application

        val database = NorrisDatabase.getInstance(application).userDao

        val userViewModelFactory = UserViewModelFactory(database, application)

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote, container, false)

        apiQuoteViewModel = ViewModelProviders.of(this).get(ApiQuoteViewModel::class.java)

        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        binding.viewModel = apiQuoteViewModel

        currentUserId = application.getSharedPreferences("current_user", Context.MODE_PRIVATE)
            .getLong("current_user", 0)

        favourite =
            ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.ic_favourite)
        favouriteBorder =
            ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.ic_favourite_border)

        binding.lifecycleOwner = this

        //Load gif loader
        Glide.with(this).load(Uri.parse("file:///android_asset/loader.gif"))
            .error(R.drawable.ic_chuck_norris_white)
            .into(binding.loader)

        binding.quoteText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                binding.favourite.setImageDrawable(favouriteBorder)
                binding.favourite.tag = R.string.favourite_border
            }
        })

        binding.favourite.setOnClickListener {
            if (binding.favourite.tag == R.string.favourite) {
                binding.favourite.setImageDrawable(favouriteBorder)
                binding.favourite.tag = R.string.favourite_border
                removeFavourite()
            } else {
                binding.favourite.setImageDrawable(favourite)
                binding.favourite.tag = R.string.favourite
                addFavourite()
            }
        }
        return binding.root
    }

    /**
     * Remove quote from user's favourite list
     */
    private fun removeFavourite() {
        userViewModel.removeFavourite(currentUserId, quoteText.text.toString())
        { isRemoved: Boolean, _: User? ->
            requireNotNull(this.activity).runOnUiThread {
                if (isRemoved) {
                    Toasty.normal(
                        activity!!.applicationContext,
                        R.string.remove_favourite,
                        Toast.LENGTH_SHORT,
                        favouriteBorder
                    ).show()
                } else {
                    Toasty.error(
                        activity!!.applicationContext,
                        "Sorry ! Chuck Norris doesn't want that."
                    )
                }
            }
        }

    }

    /**
     * Add quote to user's favourite list
     */
    private fun addFavourite() {
        userViewModel.addFavourites(currentUserId, quoteText.text.toString())
        { isUpdated: Boolean, isLimitReached: Boolean, alreadyExist: Boolean ->
            requireNotNull(this.activity).runOnUiThread {
                // Verify favourite list size
                if (isLimitReached) {
                    Toasty.error(
                        requireNotNull(this.activity).applicationContext,
                        "You already have too much favourite quotes",
                        Toast.LENGTH_LONG
                    ).show()

                    //Verify quote uniqueness
                } else if (alreadyExist) {
                    Toasty.error(
                        requireNotNull(this.activity).applicationContext,
                        "This quote is already your favourite",
                        Toast.LENGTH_LONG
                    ).show()

                    //Verify if the change is ok
                } else if (!isUpdated) {
                    Toasty.error(
                        requireNotNull(this.activity).applicationContext,
                        "Chuck Norris don't want you to add this to your favourites",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toasty.normal(
                        activity!!.applicationContext,
                        R.string.add_favourite,
                        Toast.LENGTH_SHORT,
                        favourite
                    ).show()

                }
            }
        }
    }

}

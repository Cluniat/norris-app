package com.example.norris_app.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.norris_app.R
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.databinding.FragmentProfileInfoBinding
import com.example.norris_app.model.User
import com.example.norris_app.utils.Utils
import com.example.norris_app.viewmodel.CountriesViewModel
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.CountriesViewModelFactory
import com.example.norris_app.viewmodelfactory.UserViewModelFactory
import es.dmoral.toasty.Toasty


class ProfileInfoFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var countriesViewModel: CountriesViewModel
    private lateinit var binding: FragmentProfileInfoBinding
    private lateinit var spinner: Spinner
    private lateinit var countries: ArrayList<String>
    private lateinit var currentUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application

        val database = NorrisDatabase.getInstance(application).userDao

        val userViewModelFactory = UserViewModelFactory(database, application)

        val countryViewModelFactory = CountriesViewModelFactory()

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_info, container, false)

        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        countriesViewModel =
            ViewModelProviders.of(this, countryViewModelFactory).get(CountriesViewModel::class.java)

        val currentUserId = application.getSharedPreferences("current_user", Context.MODE_PRIVATE)
            .getLong("current_user", 0)
        getCurrentUser(currentUserId)

        binding.edit.setOnClickListener { binding.isEditMode = true }
        binding.validateButton.setOnClickListener { updateUser() }
        binding.favourite.setOnClickListener { navigateToFavourite(it) }

        binding.lifecycleOwner = this

        spinner = binding.countrySpinner
        countries = countriesViewModel.getCountries()
        val spinnerArrayAdapter =
            ArrayAdapter<String>(
                application,
                android.R.layout.simple_spinner_dropdown_item,
                countries
            )
        spinnerArrayAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerArrayAdapter.insert("Country", 0)
        spinner.adapter = spinnerArrayAdapter
        spinner.setSelection(0)

        return binding.root
    }

    /**
     * Get the user connected and display its information
     */
    private fun getCurrentUser(id: Long) {
        userViewModel.getUserById(id) { user: User? ->
            requireNotNull(this.activity).runOnUiThread {
                if (user != null) {
                    currentUser = user

                    if (user.lastname != null && user.lastname != "") {
                        binding.lastnameTV.visibility = View.GONE
                        binding.lastnameEditText.setText(user.lastname)
                        binding.lastnameTV.text = user.lastname
                    } else {
                        binding.lastnameEditText.hint = "lastname"
                        binding.lastnameTV.visibility = View.GONE
                    }

                    if (user.firstname != null && user.firstname != "") {
                        binding.firstnameTV.visibility = View.VISIBLE
                        binding.firstnameEditText.setText(user.firstname)
                        binding.firstnameTV.text = user.firstname
                    } else {
                        binding.firstnameEditText.setHint(R.string.firstname)
                        binding.firstnameTV.visibility = View.GONE
                    }

                    if (user.email != null || user.email != "") {
                        binding.emailTV.visibility = View.VISIBLE
                        binding.emailEditText.setText(user.email)
                        binding.emailTV.text = user.email
                    } else {
                        binding.emailEditText.setHint(R.string.email)
                        binding.emailTV.visibility = View.GONE

                    }

                    if (user.birthdayDate != null && user.birthdayDate != "") {
                        binding.birthdayTV.visibility = View.VISIBLE
                        binding.birthdayEditText.setText(user.birthdayDate)
                        binding.birthdayTV.text = user.birthdayDate
                    } else {
                        binding.birthdayEditText.setHint(R.string.birthday)
                        binding.birthdayTV.visibility = View.GONE
                    }

                    if (user.address != null && user.address != "") {
                        binding.addressTV.visibility = View.VISIBLE
                        binding.addressEditText.setText(user.address)
                        binding.addressTV.text = user.address

                    } else {
                        binding.addressEditText.setHint(R.string.address)
                        binding.addressTV.visibility = View.GONE
                    }

                    if (user.city != null && user.city != "") {
                        binding.cityTV.visibility = View.VISIBLE
                        binding.cityEditText.setText(user.city)
                        binding.cityTV.text = user.city

                    } else {
                        binding.cityEditText.setHint(R.string.city)
                        binding.cityTV.visibility = View.GONE
                    }

                    if (user.country != null && user.country != "") {
                        binding.countryTV.visibility = View.VISIBLE
                        binding.countryTV.text = user.country

                        for ((index, value) in countries.withIndex()) {
                            if (value == user.country) {
                                spinner.setSelection(index)
                                return@runOnUiThread
                            }
                        }
                    } else {
                        binding.countryTV.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * Update connected user with new values
     */
    private fun updateUser() {
        val userToUpdate = currentUser
        userToUpdate.lastname = binding.lastnameEditText.text.toString()
        userToUpdate.firstname = binding.firstnameEditText.text.toString()
        userToUpdate.email = binding.emailEditText.text.toString()
        userToUpdate.city = binding.cityEditText.text.toString()
        userToUpdate.address = binding.addressEditText.text.toString()

        //Verify birthday date format
        if (Utils.isValidDate(binding.birthdayEditText.text.toString())) {
            userToUpdate.birthdayDate = binding.birthdayEditText.text.toString()
        } else {
            Toasty.error(
                requireNotNull(this.activity).applicationContext,
                "Please enter a valide date dd/MM/yyyy",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Country validation
        if (binding.countrySpinner.selectedItem.toString() != "Country") {
            userToUpdate.country = binding.countrySpinner.selectedItem.toString()
        } else {
            userToUpdate.country = ""
        }

        //update database
        userViewModel.updateUser(userToUpdate) { user: User ->
            requireNotNull(activity).runOnUiThread {
                if (user == userToUpdate) {
                    Toasty.success(
                        requireNotNull(activity).applicationContext,
                        "User updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toasty.error(
                        requireNotNull(activity).applicationContext,
                        "User not updated",
                        Toast.LENGTH_SHORT
                    )
                }
            }
        }
        //update currentUser value
        getCurrentUser(currentUser.id)

        //Display changes
        binding.isEditMode = false
    }

    /**
     * Navigate to FavouriteListFragment
     */
    private fun navigateToFavourite(view: View) {
        if (currentUser.favourites == null || currentUser.favourites == "") {
            Toasty.error(
                activity!!.applicationContext,
                "You don't have any favourite",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            view.findNavController()
                .navigate(
                    ProfileInfoFragmentDirections.actionProfileInfoFragmentToFavouriteListFragment(
                        currentUser
                    )
                )
        }
    }
}

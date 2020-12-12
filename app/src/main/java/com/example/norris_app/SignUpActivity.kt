package com.example.norris_app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.databinding.ActivitySignUpBinding
import com.example.norris_app.model.User
import com.example.norris_app.viewmodel.CountriesViewModel
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.CountriesViewModelFactory
import com.example.norris_app.viewmodelfactory.UserViewModelFactory
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import es.dmoral.toasty.Toasty
import org.mindrot.jbcrypt.BCrypt
import java.util.*


class SignUpActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var countriesViewModel: CountriesViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivitySignUpBinding
    private var datePickerDialog = DatePickerDialog()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // set theme
        setTheme(R.style.AppTheme)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_sign_up)

        val dataBase = NorrisDatabase.getInstance(application).userDao

        val userViewModelFactory = UserViewModelFactory(dataBase, application)

        val countryViewModelFactory = CountriesViewModelFactory()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)

        countriesViewModel =
            ViewModelProviders.of(this, countryViewModelFactory).get(CountriesViewModel::class.java)

        binding.lifecycleOwner = this

        // set spinner's values
        val spinner = binding.countrySpinner
        val countries = countriesViewModel.getCountries()
        val spinnerArrayAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countries)
        spinnerArrayAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = spinnerArrayAdapter

        //set default spinner value
        val actualLocale = Locale.getDefault().displayCountry
        for ((index, value) in countries.withIndex()) {
            if (value == actualLocale) {
                spinner.setSelection(index)
                return
            }
        }
    }

    /**
     * display date dialog
     */
    fun onDateClick(view: View? = null) {
        val Year = 0
        val Month = 0
        val Day = 0
        datePickerDialog = DatePickerDialog.newInstance(this@SignUpActivity, Year, Month, Day)
        datePickerDialog.isThemeDark = false
        datePickerDialog.showYearPickerFirst(false)
        datePickerDialog.setTitle("birthday")
        datePickerDialog.show(supportFragmentManager, "Birthday")
    }

    /**
     * set date variable
     */
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val monthOfYearStr =
            if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
        val dayOfMonthStr = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val date = "$dayOfMonthStr/$monthOfYearStr/$year"
        binding.birthdayEditText.setText(date)
    }

    /**
     * Assign gender value from checkbox
     */
    private fun genderToInt(): Int {
        return when {
            binding.rbMale.isChecked -> 0
            binding.rbFemale.isChecked -> 1
            else -> 2
        }
    }

    /**
     * Create user and navigate to MainActivity
     */
    fun onSignUp(view: View? = null) {
        //disable signup button
        binding.validateButton.isEnabled = false

        var error = false

        //check if fields are correctly filled
        if (binding.passwordEditText.text.isNullOrEmpty()) {
            binding.passwordInputLayout.error = "Please enter a password"
            error = true
        } else {
            binding.passwordInputLayout.error = null
        }
        if (!userViewModel.isEmailValid(binding.emailEditText.text.toString())) {
            binding.emailInputLayout.error = "Please enter a valid email"
            error = true
        }

        if (!error) {
            val userToInsert = User()
            userToInsert.lastname = binding.lastnameEditText.text.toString()
            userToInsert.firstname = binding.firstnameEditText.text.toString()
            userToInsert.birthdayDate = binding.birthdayEditText.text.toString()
            userToInsert.gender = genderToInt()
            userToInsert.email = binding.emailEditText.text.toString()
            userToInsert.password = BCrypt.hashpw(binding.passwordEditText.text.toString(), BCrypt.gensalt())
            userToInsert.city = binding.cityEditText.text.toString()
            userToInsert.address = binding.addressEditText.text.toString()
            userToInsert.country = binding.countrySpinner.selectedItem.toString()

            userViewModel.createUser(userToInsert) { isCreated: Boolean ->
                runOnUiThread {
                    if (!isCreated) {
                        Toasty.error(applicationContext, R.string.email_exist, Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toasty.success(
                            applicationContext,
                            R.string.created_account,
                            Toast.LENGTH_LONG
                        ).show()
                        val mainActivityIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainActivityIntent)
                    }
                }
            }
        }
        // Enable signup button
        binding.validateButton.isEnabled = true
    }
}

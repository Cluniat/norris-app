package com.example.norris_app

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.databinding.ActivityMainBinding
import com.example.norris_app.model.User
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.UserViewModelFactory
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //set theme
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)

        val dataBase = NorrisDatabase.getInstance(application).userDao

        val viewModelFactory = UserViewModelFactory(dataBase, application)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)

        binding.lifecycleOwner = this


        // trigger animation
        object : CountDownTimer(10, 1000) {
            override fun onFinish() {
                rootView.setBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.colorSplashText
                    )
                )
                chuckNorrisImageView.setImageResource(R.drawable.ic_chuck_norris)
                startAnimation()
            }

            override fun onTick(p0: Long) {}
        }.start()
    }

    /**
     * start the icon animation
     */
    private fun startAnimation() {
        chuckNorrisImageView.animate().apply {
            x(50f)
            y(100f)
            duration = 1000
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                afterAnimationView.visibility = VISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })
    }

    /**
     * display toast on click on 'forget password'
     */
    fun onForgetPassword(view: View? = null) {
        val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_chuck_norris_white);
        Toasty.normal(
            applicationContext,
            R.string.forget_pass_response,
            icon
        ).show()
    }

    /**
     * Navigate to SignUpActivity
     */
    fun onSignUp(view: View? = null) {
        val signUpActivityIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpActivityIntent)
    }

    /**
     * verify email and password and navigate to HomeActivity
     */
    fun onLogin(view: View? = null) {
        // Disable login button
        binding.loginButton.isEnabled = false

        var error = false

        // Check if fields are correctly filled
        if (binding.emailEditText.text.isNullOrEmpty()) {
            binding.emailInputLayout.error = getString(R.string.wrong_email)
            error = true
        } else
            binding.emailInputLayout.error = null

        if (binding.passwordEditText.text.isNullOrEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.wrong_password)
            error = true
        } else
            binding.passwordInputLayout.error = null

        // check if user exist
        if (!error) {
            userViewModel.checkLogin(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            ) { isValid: Boolean, currentUser: User? ->
                runOnUiThread {
                    if (!isValid) {
                        Toasty.error(
                            applicationContext,
                            R.string.wrong_login,
                            Toast.LENGTH_LONG,
                            true
                        ).show()
                    } else {
                        if (currentUser != null) {
                            // save user id in shared preferences
                            applicationContext.getSharedPreferences(
                                "current_user",
                                Context.MODE_PRIVATE
                            )
                                .edit()
                                .putLong("current_user", currentUser.id)
                                .apply()
                        }
                        //navigate to HomeActivity
                        val homeActivityIntent = Intent(this, HomeActivity::class.java)
                        startActivity(homeActivityIntent)
                    }
                }
            }

        }
        // Enable login button
        binding.loginButton.isEnabled = true
    }

}







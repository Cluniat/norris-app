package com.example.norris_app

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.norris_app.database.NorrisDatabase
import com.example.norris_app.utils.MediaManager
import com.example.norris_app.viewmodel.UserViewModel
import com.example.norris_app.viewmodelfactory.UserViewModelFactory

class ProfilActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide action bar
        setTheme(R.style.AppTheme)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_profil)

        val dataBase = NorrisDatabase.getInstance(application).userDao

        val userViewModelFactory = UserViewModelFactory(dataBase, application)

        userViewModel =
            ViewModelProviders.of(this, userViewModelFactory).get(UserViewModel::class.java)
    }

    /**
     * Navigate to HomeActivity
     */
    fun navigateToHome(view: View? = null) {
        val homeActivityIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeActivityIntent)
    }

    /**
     * log out user and navigate to MainActivity
     */
    fun logout(view: View? = null) {
        applicationContext.getSharedPreferences(
            "current_user",
            Context.MODE_PRIVATE
        )
            .edit()
            .remove("current_user")
            .apply()

        val loginActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(loginActivityIntent)
    }

    override fun onPause() {
        super.onPause()
        MediaManager.getInstance(this@ProfilActivity).mp.pause()
    }

    override fun onResume() {
        super.onResume()
        MediaManager.getInstance(this@ProfilActivity).mp.start()
    }
}

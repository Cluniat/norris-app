package com.example.norris_app

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.norris_app.utils.MediaManager
import com.example.norris_app.viewmodel.ApiQuoteViewModel
import com.example.norris_app.viewmodelfactory.ApiQuoteViewModelFactory
import es.dmoral.toasty.Toasty


class HomeActivity : AppCompatActivity() {
    private lateinit var apiQuoteViewModel: ApiQuoteViewModel
    private var hitCpt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set theme
        setTheme(R.style.AppTheme)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val ring = MediaManager.getInstance(this@HomeActivity).mp
        ring.isLooping = true
//        if(!ring.isPlaying) ring.start()


        setContentView(R.layout.activity_home)

        val viewModelFactory = ApiQuoteViewModelFactory()
        apiQuoteViewModel = ViewModelProviders.of(
            this, viewModelFactory
        ).get(ApiQuoteViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        MediaManager.getInstance(this@HomeActivity).mp.pause()
    }

    override fun onResume() {
        super.onResume()
        MediaManager.getInstance(this@HomeActivity).mp.start()
    }

    /**
     * Navigate to ProfilActivity on click on profil icon
     */
    fun onProfil(view: View? = null) {
        val profilActivityIntent = Intent(this, ProfilActivity::class.java)
        startActivity(profilActivityIntent)
    }

    fun onChuck(view: View? = null) {
        hitCpt++
        val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_chuck_norris_white);
        Toasty.normal(
            applicationContext,
            "You can hit Chuck Norris $hitCpt times but not ${hitCpt + 1} times",
            icon
        ).show()
    }


}

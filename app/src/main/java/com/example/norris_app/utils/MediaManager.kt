package com.example.norris_app.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.norris_app.R

class MediaManager private constructor(context: Context, var mp: MediaPlayer) {

    private val mContext: Context = context.applicationContext


    companion object {

        private var sInstance: MediaManager? = null

        fun getInstance(context: Context): MediaManager {
            if (null == sInstance) {
                synchronized(MediaManager::class.java) {
                    sInstance = MediaManager(context, MediaPlayer.create(context, R.raw.walker))
                }
            }
            return sInstance!!
        }
    }
}
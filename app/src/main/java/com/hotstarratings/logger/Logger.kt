package com.hotstarratings.logger

import android.util.Log
import com.hotstarratings.BuildConfig

/**
 * Created by kaustubh on 26/11/17.
 */
class Logger {
    companion object {
        fun log(tag : String, text : String) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, text)
            }
        }
    }
}
package com.hotstarratings.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import com.hotstarratings.R
import com.hotstarratings.logger.Logger
import com.hotstarratings.models.Movie
import com.hotstarratings.network.MovieFetcher
import retrofit2.Call
import retrofit2.Response
import rx.Observable
import rx.schedulers.Schedulers


/**
 * Created by kaustubh on 26/11/17.
 */
class AccessScreenService : AccessibilityService() {
    private var mRatingView : View? = null
    private var mWindowManager : WindowManager? = null

    companion object {
        val TAG = "##AccessScreenService"
    }

    override fun onInterrupt() {
        removeView()
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event?.source
        source?.let {
            if (source.childCount == 0) return
            for (i in 0 until it.childCount) {
                val info = it.getChild(i)
                info?.let {
                    if (info.className == "android.widget.TextView") {
                        Logger.log(TAG, "event " + event.eventType)
                        it.text?.let {
                            if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.eventType) {
                                Logger.log(TAG, "-> " + it.toString())

                                getMovie(it.toString())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe ({
                                            result ->
                                            Logger.log(TAG, result)
                                            addView(result)
                                        }, { error ->
                                            error.printStackTrace()
                                            removeView()
                                        })
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeView()
    }

    fun getMovie(title: String): Observable<String> {
        val api = MovieFetcher()
        return Observable.create { subsriber ->
            val callResponse = api.getMovie(title)
            callResponse.enqueue(object : retrofit2.Callback<List<Movie>>{
                override fun onFailure(call: Call<List<Movie>>?, t: Throwable?) {
                    subsriber.onError(t)
                }

                override fun onResponse(call: Call<List<Movie>>?, response: Response<List<Movie>>?) {
                    val list = response?.body()
                    subsriber.onNext(list!![0].rating)
                    subsriber.onCompleted()
                }
            })
        }
    }

    private fun addView(rating: String) {

        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.x = 0
        params.y = 1900

        if (mRatingView == null) {
            mRatingView = LayoutInflater.from(applicationContext).inflate(R.layout.movie_rating, null)
            mWindowManager?.addView(mRatingView, params)
        }

        mRatingView?.let {
            it.findViewById<TextView>(R.id.rating).text = rating + " /10"
            it.visibility = View.VISIBLE
        }
    }

    private fun removeView() {
        mRatingView?.let {
            mWindowManager?.removeView(mRatingView)
        }
    }
}

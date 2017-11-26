package com.hotstarratings.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
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
    companion object {
        val TAG = "##AccessScreenService"
    }

    override fun onInterrupt() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val source = event?.source
        source?.let {
            if (source.childCount == 0) return
            for (i in 0 until it.childCount) {
                val info = it.getChild(i)
                info?.let {
                    if (info.className == "android.widget.TextView") {
                        // Make API call...????
                        it.text?.let {
                            if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.eventType) {
                                Logger.log(TAG, "-> " + it.toString())

                                getMovie(it.toString())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe ({
                                            result ->
                                            Logger.log(TAG, result)
                                        }, { error ->
                                            error.printStackTrace()
                                        })
                            }
                        }
                    }
                }
            }
        }
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
}

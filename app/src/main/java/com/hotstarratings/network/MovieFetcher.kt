package com.hotstarratings.network

import com.hotstarratings.common.Constants
import com.hotstarratings.models.Movie
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by kaustubh on 26/11/17.
 */
class MovieFetcher() {
    private var mApi: IMDBApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        mApi = retrofit.create(IMDBApi::class.java)
    }

    fun getMovie(title: String): Call<List<Movie>> {
        return mApi.getMovie(title)
    }
}
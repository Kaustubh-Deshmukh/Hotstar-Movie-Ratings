package com.hotstarratings.network

import com.hotstarratings.models.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kaustubh on 26/11/17.
 */
interface IMDBApi {
    @GET("/api/find/movie")
    fun getMovie(@Query("title") title: String) : Call<List<Movie>>
}
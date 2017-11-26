package com.hotstarratings.models

/**
 * Created by kaustubh on 26/11/17.
 */
class MoviesResponse(val list: List<Movie>)

class Movie(val title: String, val rating: String, val director: String, val year: String)
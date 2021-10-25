package com.itcomca.testfactum.views.mainactivity.interfaces

import com.google.gson.JsonObject

interface IJSONMovies {
    fun GetObject(movie: JsonObject?, textTime: String)
}
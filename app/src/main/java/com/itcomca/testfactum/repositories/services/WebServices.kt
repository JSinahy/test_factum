package com.itcomca.testfactum.repositories.services

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WebServices {

    /** Se crea el llamado al primer endpoint para el encabezado del disenio **/
    @GET("movie/now_playing?language=en-US&page=undefined")
    fun GetPlayingMovies(@Query("api_key") API_KEY: String) : Call<JsonObject>

    /** Se crea el llamado al primer endpoint para el la lista del disenio **/
    @GET("movie/popular?language=en-US&page=6")
    fun GetPopularMovies(@Query("api_key") API_KEY: String) : Call<JsonObject>

    /** Se crea el llamado al primer endpoint para llamar la info de una movie en especifico **/
    @GET("movie/{movieId}?language=en-US")
    fun GetPopularMovieById(@Path("movieId") movieId: String, @Query("api_key") API_KEY: String) : Call<JsonObject>

}
package com.itcomca.testfactum.repositories.remote

import com.google.gson.JsonObject
import com.itcomca.testfactum.base.BaseApplication
import com.itcomca.testfactum.base.ListenerResponses
import com.itcomca.testfactum.base.Operations
import com.itcomca.testfactum.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularMovieRepository {
    private val webServices = BaseApplication.getRetrofitInstance()

    fun get(listener: ListenerResponses){
        webServices.GetPopularMovies(Constants.API_KEY).enqueue(object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    listener.onSuccess(false, Operations.POPULAR_MOVIES, response.body()!!)
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                listener.onError(true, t.message.toString())
            }

        })
    }

    fun getById(listener: ListenerResponses, idMovie: String) {
        webServices.GetPopularMovieById(idMovie, Constants.API_KEY).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        listener.onSuccess(false, Operations.DETAILS_MOVIE, response.body()!!)
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    listener.onError(true, t.message.toString())
                }
            })
    }
}
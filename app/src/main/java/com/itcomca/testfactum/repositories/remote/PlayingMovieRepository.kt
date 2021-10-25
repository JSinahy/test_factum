package com.itcomca.testfactum.repositories.remote


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import com.itcomca.testfactum.base.BaseApplication
import com.itcomca.testfactum.base.ListenerResponses
import com.itcomca.testfactum.base.Operations
import com.itcomca.testfactum.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayingMovieRepository {
    private val webServices = BaseApplication.getRetrofitInstance()

    fun get(listener: ListenerResponses){
        webServices.GetPlayingMovies(Constants.API_KEY).enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful){
                    listener.onSuccess(false, Operations.PLAYING_MOVIES, response.body()!!)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                listener.onError(true, t.message.toString())
            }

        })
    }

}
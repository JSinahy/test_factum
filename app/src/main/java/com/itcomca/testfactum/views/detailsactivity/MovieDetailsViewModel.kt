package com.itcomca.testfactum.views.detailsactivity

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.itcomca.testfactum.base.BaseViewModel
import com.itcomca.testfactum.base.ListenerResponses
import com.itcomca.testfactum.base.Operations
import com.itcomca.testfactum.repositories.remote.PopularMovieRepository

class MovieDetailsViewModel:BaseViewModel(), ListenerResponses {
    var mJsonDataDetails: MutableLiveData<JsonObject> = MutableLiveData()

    fun executeGetDetailsMovie(idMovie: String){
        mLoadingLiveData.postValue(true)
        PopularMovieRepository().getById(this, idMovie)
    }

    override fun onSuccess(error: Boolean, operation: Operations, data: JsonObject) {
        when(operation){
            Operations.DETAILS_MOVIE -> {
                mJsonDataDetails.postValue(data)
            }
        }
    }

    override fun onError(error: Boolean, msg: String) {

    }

}
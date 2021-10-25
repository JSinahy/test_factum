package com.itcomca.testfactum.views.homefragment

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.itcomca.testfactum.base.BaseViewModel
import com.itcomca.testfactum.base.ListenerResponses
import com.itcomca.testfactum.base.Operations
import com.itcomca.testfactum.repositories.remote.PlayingMovieRepository
import com.itcomca.testfactum.repositories.remote.PopularMovieRepository

class HomeViewModel: BaseViewModel(), ListenerResponses {

    /** Este es el dato que se estara observando en su cambio, se llana cuando se regresa la info del endpoint **/
    var mJsonDataPlaying: MutableLiveData<JsonObject> = MutableLiveData()
    var mJsonDataPopular: MutableLiveData<JsonObject> = MutableLiveData()

    /** Se ejecuta el llamado del repositorio remoto PlayingMovies **/
    fun executeGetPlayingMovies(){
        mLoadingLiveData.postValue(true)
        PlayingMovieRepository().get(this)
    }
    /** Se ejecuta el llamado del repositorio remoto PopularMovies **/
    fun executeGetPopularMovies(){
        mLoadingLiveData.postValue(true)
        PopularMovieRepository().get(this)
    }

    /** Se capturan las respuestas del llamado al servicio de red, SI ES EXITOSA **/
    override fun onSuccess(error: Boolean, operation: Operations, data: JsonObject) {
        when(operation){
            /** Se valida el tipo de operacion que se mando a llamar **/
            Operations.PLAYING_MOVIES -> {
                mLoadingLiveData.postValue(false)
                mErrorLiveData.postValue(false)
                mJsonDataPlaying.postValue(data)
            }
            Operations.POPULAR_MOVIES -> {
                mLoadingLiveData.postValue(false)
                mErrorLiveData.postValue(false)
                mJsonDataPopular.postValue(data)
            }
        }
    }

    /** Se capturan las respuestas del llamado al servicio de red, SI HAY ERROR **/
    override fun onError(error: Boolean, msg: String) {
        /** Ocultar el loading, porque acabo la operacion **/
        mLoadingLiveData.postValue(false)

        /** Setear de que hubo un error **/
        mErrorLiveData.postValue(error)

        /** Setear el mensaje devuelto por el error obtenido **/
        mMessageLiveData.postValue(msg)

        /** Setear el valor de vacio en true porque como hubo un error **/
        mIsEmpty.postValue(true)
    }
}
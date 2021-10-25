package com.itcomca.testfactum.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    /** Boolean para saber si hay o no errores **/
    protected val mErrorLiveData = MutableLiveData<Boolean>()
    val errorLiveData: LiveData<Boolean> get() = mErrorLiveData

    /** String para conocer el mensaje de los resultados **/
    protected val mMessageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String> get() = mMessageLiveData

    /** Boolean para conocer si debe o no mostrar el Loading **/
    protected val mLoadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> get() = mLoadingLiveData

    /** Boolean para conocer si el resultado es vacio o no **/
    protected val mIsEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = mIsEmpty

}
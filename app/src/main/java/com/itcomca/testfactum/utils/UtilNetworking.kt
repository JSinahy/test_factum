package com.itcomca.testfactum.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.itcomca.testfactum.base.BaseApplication
import okhttp3.Cache

class UtilNetworking {
    companion object{
        fun hasNetwork(): Boolean?{
            var isConnected: Boolean? = false
            val connectivityManager = BaseApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if(activeNetwork != null && activeNetwork.isConnected) isConnected = true
            return isConnected
        }

        fun getCacheSize() = (5 * 1024 * 1024).toLong()

        fun getCache() = Cache(BaseApplication.applicationContext().cacheDir, getCacheSize())

    }

}
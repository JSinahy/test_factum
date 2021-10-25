package com.itcomca.testfactum.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import androidx.room.Room
import com.itcomca.testfactum.repositories.services.APIMoviesDB
import com.itcomca.testfactum.repositories.services.WebServices

class BaseApplication: Application() {

    companion object {
        /** Static de solo lectura **/
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: BaseApplication
            private set

        /** Set activity actual **/
        @SuppressLint("StaticFieldLeak")
        lateinit var currentActivity: Activity

        //lateinit var room: EthicalDatabase;
        /** Contexto general de la aplicacion **/
        fun applicationContext() = instance.applicationContext;

        /** Activity actual activo en el foreground **/
        fun currentActivity() = currentActivity
        //fun getRoomDatabase() = room

        /** Instancia general del retrofit para llamarlo desde cualquier lado de la aplicacion **/
        fun getRetrofitInstance() = APIMoviesDB().GetInstance().create(WebServices::class.java)
    }

    override fun onCreate(){
        super.onCreate()
        instance = this
        // fallbackToDestructiveMigration esto quitarlo para produccion
        //room = Room.databaseBuilder(this, EthicalDatabase::class.java, "ethicalmedic").fallbackToDestructiveMigration().build()
    }
}
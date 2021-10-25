package com.itcomca.testfactum.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Se setea el activity actual en ejecucion **/
        BaseApplication.currentActivity = this
    }

    /** Metodo que manda a llamar a un fragment desde un activity base **/
    fun ReplaceFragment(fragment: Fragment, container: Int){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(container, fragment)
        fragmentTransaction.commit()
    }
}
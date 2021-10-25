package com.itcomca.testfactum.views.mainactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.itcomca.testfactum.R
import com.itcomca.testfactum.base.BaseActivity
import com.itcomca.testfactum.databinding.ActivityMainBinding
import com.itcomca.testfactum.repositories.models.MovieDetailModel
import com.itcomca.testfactum.views.camerafragment.CameraFragment
import com.itcomca.testfactum.views.detailsactivity.MovieDetailsActivity
import com.itcomca.testfactum.views.homefragment.HomeFragment
import com.itcomca.testfactum.views.mainactivity.adapters.PlayingAdapter
import com.itcomca.testfactum.views.mainactivity.adapters.PopularAdapter
import com.itcomca.testfactum.views.mainactivity.interfaces.IJSONMovies
import com.itcomca.testfactum.views.mapafragment.MapaFragment

class MainActivity : BaseActivity() {

    /** Variable que controlara el databinding **/
    lateinit var binding: ActivityMainBinding

    private lateinit var playingAdapter : PlayingAdapter
    private lateinit var popularAdapter : PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** Binding de la vista del activity **/
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ReplaceFragment(HomeFragment(), binding.fragmentContainer.id)

        SetupListener()
    }

    fun SetupListener(){
        binding.imgCamera.setOnClickListener {
            ReplaceFragment(CameraFragment(), binding.fragmentContainer.id)
        }
        binding.imgLocation.setOnClickListener {
            ReplaceFragment(MapaFragment(), binding.fragmentContainer.id)
        }
    }



}
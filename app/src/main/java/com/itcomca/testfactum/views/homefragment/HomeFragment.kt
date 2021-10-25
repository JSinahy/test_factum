package com.itcomca.testfactum.views.homefragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.itcomca.testfactum.R
import com.itcomca.testfactum.base.BaseApplication
import com.itcomca.testfactum.databinding.FragmentHomeBinding
import com.itcomca.testfactum.repositories.models.MovieDetailModel
import com.itcomca.testfactum.views.detailsactivity.MovieDetailsActivity
import com.itcomca.testfactum.views.mainactivity.MainActivityViewModel
import com.itcomca.testfactum.views.mainactivity.adapters.PlayingAdapter
import com.itcomca.testfactum.views.mainactivity.adapters.PopularAdapter
import com.itcomca.testfactum.views.mainactivity.interfaces.IJSONMovies

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), IJSONMovies {

    /** Variable que controlara el databinding **/
    lateinit var binding:FragmentHomeBinding

    private lateinit var playingAdapter : PlayingAdapter
    private lateinit var popularAdapter : PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        /** Metodos de inicializacion **/
        InitAdapters()
        SetupObservables()

        return binding.root
    }

    /** Se inicializan los adaptadores **/
    fun InitAdapters(){
        playingAdapter = PlayingAdapter()
        popularAdapter = PopularAdapter(this)
    }

    /** Se setean los observables para cuando se devuelva y este lista la informacion **/
    @SuppressLint("NotifyDataSetChanged")
    fun SetupObservables(){
        /** Variable para el viewModel del activity **/
        val viewModel: HomeViewModel by viewModels()

        viewModel.mJsonDataPlaying.observe(this, {
            playingAdapter.items = it["results"] as JsonArray
            playingAdapter.notifyDataSetChanged()

            /** Se coloca la informacion como fuente del adapter **/
            binding.recyclerPlaying.adapter = playingAdapter

        })
        viewModel.mJsonDataPopular.observe(this, {
            popularAdapter.items = it["results"] as JsonArray
            popularAdapter.notifyDataSetChanged()

            /** Se coloca la informacion como fuente del adapter **/
            binding.recyclerPopular.adapter = popularAdapter
        })
        /** Se mandan a llamar los metodos para obtener la informacion **/
        viewModel.executeGetPopularMovies()
        viewModel.executeGetPlayingMovies()
    }

    /** Metodo de la interfaz que se manda a llamar cuando le doy click a uno item del recycler **/
    override fun GetObject(movie: JsonObject?, textTime: String) {
        if(movie != null){
            val movieDetail = MovieDetailModel()
            movieDetail.movieId = movie["id"].asString
            movieDetail.pathImage = movie["poster_path"].asString
            movieDetail.title = movie["title"].asString
            movieDetail.subtitle = movie["release_date"]?.asString.toString()
            movieDetail.sinopsis = movie["overview"].asString

            /** Argumentos pasados a la siguiente actividad **/
            val args = Bundle()
            args.putSerializable("DETAILS", movieDetail)
            args.putString("TIEMPO", textTime)

            /** Metodo del BaseActivity para mandar a llamar un Activity **/
            val intent = Intent(BaseApplication.applicationContext(), MovieDetailsActivity::class.java).apply {
                putExtra("DETAILS", movieDetail)
                putExtra("TIEMPO", textTime)
            }
            activity?.startActivity(intent)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
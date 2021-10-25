package com.itcomca.testfactum.views.detailsactivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.itcomca.testfactum.R
import com.itcomca.testfactum.base.BaseActivity
import com.itcomca.testfactum.databinding.ActivityMovieDetailsBinding
import com.itcomca.testfactum.repositories.models.MovieDetailModel
import com.itcomca.testfactum.utils.Constants
import com.itcomca.testfactum.utils.UtilFormatter

class MovieDetailsActivity : BaseActivity() {

    private var binding : ActivityMovieDetailsBinding? = null;

    private lateinit var poster : ImageView
    private var movie = MovieDetailModel()
    private var textTime : String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details)

        poster = binding?.poster!!

        GetExtraData()
        SetExtraData()
        CallShipsMovie()
        SetupListener()
    }

    /** Se crea el listener click para cuando se le da click a la flecha hacia la izquierda **/
    fun SetupListener() {
        binding?.back?.setOnClickListener {
            finish()
        }
    }

    /** Se setean los datos extras obtenidos **/
    fun SetExtraData(){
        if (title != null){
            binding?.textTitle?.text = movie.title;
            if(movie.subtitle != "null"){
                binding?.textSubtitle?.text = "${UtilFormatter.formatDate(movie.subtitle!!)} - ${ textTime }"
            }else binding?.textSubtitle?.text =  "- ${ textTime }"

            binding?.textSinapsis?.text = movie.sinopsis

            /** Se carga la imagen desde la ruta que viene de la informacion ontenida de los extras **/
            Glide.with(this).load(Constants.baseImage + movie.pathImage).into(poster)
        }
    }

    fun GetExtraData(){
        /** Se obtienen los valores extras que se enviando del MainActivity al Detail **/
        movie = intent.getSerializableExtra("DETAILS") as MovieDetailModel
        textTime = intent.getStringExtra("TIEMPO").toString()
    }

    /** Metodo que en tiempo de ejecucion crea los generos de las peliculas **/
    fun CallShipsMovie(){
        val viewModel: MovieDetailsViewModel by viewModels()
        viewModel.mJsonDataDetails.observe(this, {
            /** Se obtienen los generos en forma JsonObject y se saca el array de los generos **/
            var genres = it["genres"] as JsonArray

            /** Se recorre el array para conocer los items de los generos de la pelicula **/
            for(i in 0..genres.size() - 1 ){
                /** Se obtiene un genero dependiendo del indice del recorrimiento **/
                val objeto = genres.get(i) as JsonObject

                /** Se crea en tiempo de ejecucion un TextView con Style Ships **/
                val control = TextView(this@MovieDetailsActivity, null, 0, R.style.Ships)

                /** Al textView se le setea el texto depenediendo del nombre del genero obtenido del arreglo **/
                control.text = objeto["name"].asString.toUpperCase()

                /** Se le setean los Layouts WRAP_CONTENT para width y WRAP_CONTENT para height **/
                val params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

                /** Se setean los margenes **/
                params.setMargins(0, 16, 16, 0)

                /** Se aniaden al control creado  **/
                control.layoutParams = params

                /** El control se aniade al control que lo va a contener **/
                binding?.lblShips?.addView(control)
            }
        })
        /** Se realiza el llamado del endpoint para obtener los generos y la informacion de una pelicula en especifico **/
        viewModel.executeGetDetailsMovie(movie.movieId)
    }
}
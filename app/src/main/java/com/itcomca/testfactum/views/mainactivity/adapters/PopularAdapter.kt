package com.itcomca.testfactum.views.mainactivity.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.backbase.assignment.ui.custom.RatingView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.itcomca.testfactum.BuildConfig
import com.itcomca.testfactum.R
import com.itcomca.testfactum.utils.Constants

import com.itcomca.testfactum.views.mainactivity.interfaces.IJSONMovies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PopularAdapter(var listener: IJSONMovies, var items: JsonArray = JsonArray()) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_movies_popular,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: JsonObject? = items[position] as JsonObject
        if (data != null) {
            holder.bind(data)
            val json: JsonObject = items[position] as JsonObject
            holder.row.setOnClickListener {
                listener.GetObject(json, holder.textTime.text as String)
            }
        }
    }

    override fun getItemCount() = items.size()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var poster: ImageView
        lateinit var title: TextView
        lateinit var releaseDate: TextView
        lateinit var rating: RatingView
        lateinit var row: RelativeLayout
        lateinit var textTime: TextView

        fun fetchPopularMovies(movieId: String) : JsonPrimitive {
            val jsonString = URL("${BuildConfig.BASE_URL}/movie/${ movieId }?language=en-US&api_key=${Constants.API_KEY}").readText()
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            return jsonObject["runtime"] as JsonPrimitive
        }

        @SuppressLint("ResourceType", "SetTextI18n")
        fun bind(item: JsonObject?) = with(itemView) {
            poster = itemView.findViewById(R.id.poster)
            title = itemView.findViewById(R.id.textTitle)
            releaseDate = itemView.findViewById(R.id.releaseDate)
            rating = itemView.findViewById(R.id.rating)
            textTime = itemView.findViewById(R.id.textTime)

            if (item != null) {
                Glide
                    .with(poster)
                    .load("https://image.tmdb.org/t/p/original/${item["poster_path"].asString}")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(poster);

                title.text = item["title"].asString

                row = itemView.findViewById(R.id.row)

                if ((item["vote_average"].asDouble * 10) >= 51) { // Para pintar el circulo de progress de color Verde
                    val color: Int = Color.parseColor("#214628")
                    rating.setProgColor(Color.parseColor("#1fd07a"))
                    rating.setBackColor(color)
                    rating.setFirstColor(color)
                    rating.setStartColor(color)
                    val ratingValue: Int = (item["vote_average"].asInt * 10)
                    rating.setProgress(ratingValue)
                }else if((item["vote_average"].asDouble * 10) >= 21){ // Para pintar el circulo del progress de color Amarillo
                    val color: Int = Color.parseColor("#413e0e")
                    rating.setProgColor(Color.parseColor("#d1d530"))
                    rating.setBackColor(color)
                    rating.setFirstColor(color)
                    rating.setStartColor(color)
                    val ratingValue: Int = (item["vote_average"].asInt * 10)
                    rating.setProgress(ratingValue)
                }else {
                    val color: Int = Color.parseColor("#5f0404") // Para pintar el cirulo del progress de color rojo
                    rating.setProgColor(Color.parseColor("#F44336"))
                    rating.setBackColor(color)
                    rating.setFirstColor(color)
                    rating.setStartColor(color)
                    val ratingValue: Int = (item["vote_average"].asInt * 10)
                    rating.setProgress(ratingValue)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    val tiempo = fetchPopularMovies(item["id"].asString)
                    withContext(Dispatchers.Main){
                        val hours = tiempo.asInt.div(60).toString()
                        val min = tiempo.asInt.rem(60).toString()
                        textTime.text = "${ hours }h ${ min }m"
                    }
                }
            }

        }
    }
}

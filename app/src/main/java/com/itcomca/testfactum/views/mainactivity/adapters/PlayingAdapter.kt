package com.itcomca.testfactum.views.mainactivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.itcomca.testfactum.R
import com.itcomca.testfactum.utils.Constants

class PlayingAdapter(var items: JsonArray = JsonArray()) :
    RecyclerView.Adapter<PlayingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_movies_playing,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position] as JsonObject)

    override fun getItemCount() = items.size()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var poster: ImageView
        lateinit var textTitle: TextView

        fun bind(item: JsonObject) = with(itemView) {
            poster = itemView.findViewById(R.id.poster)
            textTitle = itemView.findViewById(R.id.textTitle)

            Glide
                .with(poster)
                .load(Constants.baseImage + item["poster_path"].asString)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(poster)

            textTitle.text = item["title"].asString
        }
    }
}
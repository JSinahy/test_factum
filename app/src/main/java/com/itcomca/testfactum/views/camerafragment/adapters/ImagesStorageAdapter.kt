package com.itcomca.testfactum.views.camerafragment.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.itcomca.testfactum.R
import com.itcomca.testfactum.repositories.models.ImageStorageModel

class ImagesStorageAdapter(var items: ArrayList<ImageStorageModel>) :
    RecyclerView.Adapter<ImagesStorageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.custom_items_images_storage,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val data: ImageStorageModel = items.get(position)

        holder.bind(data)
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var image: ImageView
        lateinit var name: TextView
        lateinit var path: TextView

        fun bind(item: ImageStorageModel) = with(itemView) {
            image = itemView.findViewById(R.id.imageStorage)
            name = itemView.findViewById(R.id.textName)
            path = itemView.findViewById(R.id.textPath)

            Glide.with(image).load(item.pathImage).into(image)
            name.text = item.name
        }
    }
}
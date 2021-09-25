package com.aki.realestatemanagerv2.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.R
import com.aki.realestatemanagerv2.database.entities.Picture
import com.bumptech.glide.Glide

class PictureListAdapter(
    private val dataSet: List<Picture>
) : RecyclerView.Adapter<PictureListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.model_detail_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        Glide.with(holder.itemView)
            .load(currentItem.uri)
            .into(holder.pic)

        holder.title.text = currentItem.title

    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val pic: ImageView
        val title: TextView

        init {
            pic = view.findViewById(R.id.media_pic)
            title = view.findViewById(R.id.media_txt)
        }
    }
}
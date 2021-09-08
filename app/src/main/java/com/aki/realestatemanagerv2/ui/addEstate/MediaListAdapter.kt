package com.aki.realestatemanagerv2.ui.addEstate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.R
import com.bumptech.glide.Glide
import com.aki.realestatemanagerv2.database.entities.Picture

class MediaListAdapter(private val mediaList: ArrayList<Picture>) :
    RecyclerView.Adapter<MediaListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.media_rv_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mediaList[position]
        holder.title.text = currentItem.title

        Glide.with(holder.itemView)
            .load(currentItem.uri)
            .into(holder.pic)
    }

    override fun getItemCount() = mediaList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pic: ImageView
        val title: TextView

        init {
            pic = view.findViewById(R.id.media_pic)
            title = view.findViewById(R.id.media_txt)
        }
    }
}
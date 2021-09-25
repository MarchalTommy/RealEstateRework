package com.aki.realestatemanagerv2.ui.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.R
import com.bumptech.glide.Glide
import com.aki.realestatemanagerv2.database.entities.Picture

class EditMediaAdapter(
    private val mediaList: ArrayList<Picture>,
    private val onClick: (Picture) -> Unit
) :
    RecyclerView.Adapter<EditMediaAdapter.ViewHolder>() {

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

        holder.remove.setOnClickListener {
            onClick(currentItem)
        }
    }

    override fun getItemCount() = mediaList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pic: ImageView = view.findViewById(R.id.media_pic)
        val title: TextView = view.findViewById(R.id.media_txt)
        val remove: ImageButton = view.findViewById(R.id.remove_button)

    }
}
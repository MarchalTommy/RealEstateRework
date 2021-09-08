package com.aki.realestatemanagerv2.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.R
import com.bumptech.glide.Glide
import com.aki.realestatemanagerv2.database.entities.Picture

class PictureListAdapter(
    private val dataSet: List<Picture>,
    private val isLandscape: Boolean
) : RecyclerView.Adapter<PictureListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.model_detail_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
//        if (!isLandscape) {
//            holder.picLandscape.visibility = View.GONE
            holder.picPortrait.visibility = View.VISIBLE
            Glide.with(holder.itemView)
                .load(currentItem.uri)
                .into(holder.picPortrait)

            holder.titlePortrait.text = currentItem.title
//        } else {
//            holder.picPortrait.visibility = View.GONE
//            holder.picLandscape.visibility = View.VISIBLE
//            Glide.with(holder.itemView)
//                .load(currentItem.uri)
//                .into(holder.picLandscape)

//            holder.titleLandscape.text = currentItem.title
//        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val picPortrait: ImageView
//        val picLandscape: ImageView

        val shadePortrait: View
//        val shadeLandscape: View

        val titlePortrait: TextView
//        val titleLandscape: TextView

        init {
            picPortrait = view.findViewById(R.id.media_pic_portrait)
//            picLandscape = view.findViewById(R.id.media_pic_landscape)
            shadePortrait = view.findViewById(R.id.shade_portrait)
//            shadeLandscape = view.findViewById(R.id.shade_land)
            titlePortrait = view.findViewById(R.id.media_portrait_txt)
//            titleLandscape = view.findViewById(R.id.media_land_txt)
        }
    }
}
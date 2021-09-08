package com.aki.realestatemanagerv2.ui.detail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aki.realestatemanagerv2.R
import com.google.android.material.chip.Chip

class DataDetailAdapter(
    private val dataSet: List<Int>,
    private val drawables: List<Drawable>
) :
    RecyclerView.Adapter<DataDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.model_detail_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.chip.text = "${dataSet[position]} sq.m"
                holder.chip.chipIcon = drawables[position]
            }
            1 -> {
                holder.chip.text = "${dataSet[position]} rooms"
                holder.chip.chipIcon = drawables[position]
            }
            2 -> {
                holder.chip.text = "${dataSet[position]} bedrooms"
                holder.chip.chipIcon = drawables[position]
            }
            3 -> {
                holder.chip.text = "${dataSet[position]} bathrooms"
                holder.chip.chipIcon = drawables[position]
            }
        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chip: Chip = view.findViewById(R.id.data_chip)
    }
}
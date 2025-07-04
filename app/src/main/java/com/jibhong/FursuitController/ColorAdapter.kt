package com.jibhong.FursuitController

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class ColorAdapter(
    private val colors: MutableList<Int>,
    private val onColorClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>(),
    ItemMoveCallback.ItemTouchHelperContract {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colorView: View = itemView.findViewById(R.id.viewColor)

        init {
            colorView.setOnClickListener {
                onColorClick(adapterPosition)
            }
        }

        fun bind(color: Int) {
            (colorView.background as GradientDrawable).setColor(color)
        }
    }

    // Drag & drop callbacks:
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(colors, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    // Adjusted to match interface signature
    override fun onRowSelected(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.alpha = 0.7f
    }

    override fun onRowClear(viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.alpha = 1.0f
    }
}
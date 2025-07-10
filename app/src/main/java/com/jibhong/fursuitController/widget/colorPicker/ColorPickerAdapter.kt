package com.jibhong.fursuitController.widget.colorPicker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jibhong.fursuitController.R
import java.util.Collections

class ColorPickerAdapter(
    private val colors: MutableList<Int> = mutableListOf(Color.rgb(255,255,255)),
    private val onColorClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>(),
    ColorPickerMoveCallback.ItemTouchHelperContract {

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
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onColorClick(position)
                }
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

    fun changeColor(index: Int, color: Color) {
        if (index in colors.indices) {
            colors[index] = color.toArgb()
            notifyItemChanged(index)
        }
    }

    fun addColor(){
        if(colors.size >= 10)return
        colors.add(Color.rgb(255,255,255))
        notifyItemInserted(colors.size-1)
    }

    fun removeColor(index: Int){
        if (index !in colors.indices) return
        colors.removeAt(index)
        notifyItemRemoved(index)
        if (colors.size == 0) addColor()
    }
}
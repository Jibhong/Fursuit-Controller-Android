package com.jibhong.FursuitController.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.jibhong.FursuitController.R
import java.util.*

import androidx.fragment.app.activityViewModels
import com.jibhong.FursuitController.BluetoothViewModel
import com.jibhong.FursuitController.ColorAdapter
import com.jibhong.FursuitController.ItemMoveCallback

import android.graphics.Color
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager

//import com.skydoves.colorpicker.MaterialColorPickerDialog

class LedPage : Fragment(R.layout.led_page) {

    private lateinit var adapter: ColorAdapter
    private val colorList = mutableListOf(
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
    )


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slider = view.findViewById<RangeSlider>(R.id.keyframeSlider)
        slider.values = listOf(20f, 80f)  // 👈 This enables two handles
        slider.valueFrom = 0f
        slider.valueTo = 100f

        val recyclerView = view.findViewById<RecyclerView>(R.id.ColorRecyclerView)

        adapter = ColorAdapter(colorList) { position: Int ->
            showColorPicker(position)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )

        // Attach drag & drop helper
        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showColorPicker(position: Int) {
        Toast.makeText(
            requireContext(),
            "color picker!",
            Toast.LENGTH_SHORT
        ).show()
    }
}
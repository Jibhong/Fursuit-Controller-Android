package com.jibhong.FursuitController.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.jibhong.FursuitController.ColorPickerAdapter
import com.jibhong.FursuitController.ItemMoveCallback
import com.jibhong.FursuitController.R
import com.jibhong.FursuitController.R.id.layerRecyclerView
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener


class LedPage : Fragment(R.layout.led_page) {

    private lateinit var colorAdapter: ColorPickerAdapter

    private lateinit var popupWindow: PopupWindow
    private lateinit var popupView: View
    private lateinit var root: FrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val slider = view.findViewById<RangeSlider>(R.id.keyframeSlider)
        slider.values = listOf(20f, 80f)  // 👈 This enables two handles
        slider.valueFrom = 0f
        slider.valueTo = 100f

        val colorRecyclerView = view.findViewById<RecyclerView>(R.id.ColorRecyclerView)
        val frameRecyclerView = view.findViewById<RecyclerView>(R.id.frameRecyclerView)
        val layerRecyclerView = view.findViewById<RecyclerView>(layerRecyclerView)

        colorAdapter = ColorPickerAdapter(mutableListOf(Color.RED)) { position: Int ->
            showColorPicker(position)
        }
        colorRecyclerView.adapter = colorAdapter
        colorRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )



        // Attach drag & drop helper
        val colorPickerCallback = ItemMoveCallback(colorAdapter)
        val colorPickerTouchHelper = ItemTouchHelper(colorPickerCallback)
        colorPickerTouchHelper.attachToRecyclerView(colorRecyclerView)
        popupView = LayoutInflater.from(requireContext())
            .inflate(R.layout.color_picker, null, false)

        root = view.findViewById<FrameLayout>(R.id.fragmentContainer)

        val colorAddButton = view.findViewById<ImageButton>(R.id.colorAddButton)
        colorAddButton.setOnClickListener {
            colorAdapter.addColor()
        }



    }

    private fun showColorPicker(position: Int) {

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true  // focusable so that back button dismisses it
        ).apply {
            elevation = 10f
            // optional: set animation style
            animationStyle = android.R.style.Animation_Dialog
        }
        val colorPicker = popupView.findViewById<ColorPickerView>(R.id.colorPickerView)
        val colorPickerRemoveButton = popupView.findViewById<Button>(R.id.colorPickerRemoveButton)
        colorPickerRemoveButton.setOnClickListener {
            colorAdapter.removeColor(position)
            popupWindow.dismiss()
        }
        colorPicker.setColorListener (ColorListener { color, fromUser ->
            Log.d("color", color.toColor().toString())
            colorAdapter.changeColor(position,color.toColor())
            popupWindow.dismiss()
        })
        popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0)
    }
}
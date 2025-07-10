package com.jibhong.fursuitController.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * Custom View to render a main circle and evenly distribute mini-circles (color pickers) around it.
 */
class EyeVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paint for main circle border
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = 0xFF888888.toInt()
    }

    // Data class for mini-circle
    data class MiniCircle(val position: PointF, var color: Int)

    // List of mini-circles to draw
    private val miniCircles = mutableListOf<MiniCircle>()
    private val miniRadius = 24f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = (minOf(width, height) / 2f) - circlePaint.strokeWidth

        // Draw main circle
        canvas.drawCircle(cx, cy, radius, circlePaint)

        // Draw mini-circles
        miniCircles.forEach { mini ->
            circlePaint.style = Paint.Style.FILL
            circlePaint.color = mini.color
            canvas.drawCircle(mini.position.x, mini.position.y, miniRadius, circlePaint)
        }

        // Reset style
        circlePaint.style = Paint.Style.STROKE
        circlePaint.color = 0xFF888888.toInt()
    }

    /**
     * Adds a new mini-circle at the next evenly distributed angle.
     * @param color the fill color for the mini-circle
     */
    fun addMiniCircle(color: Int) {
        val count = miniCircles.size + 1
        val angleDeg = 360f / count * (count - 1)
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val cx = width / 2f
        val cy = height / 2f
        val radius = (minOf(width, height) / 2f) - circlePaint.strokeWidth - miniRadius

        val px = (cx + radius * Math.cos(angleRad)).toFloat()
        val py = (cy + radius * Math.sin(angleRad)).toFloat()

        // Recompute positions for existing circles
        miniCircles.clear()
        for (i in 0 until count) {
            val a = Math.toRadians((360.0 / count * i))
            val x = (cx + radius * Math.cos(a)).toFloat()
            val y = (cy + radius * Math.sin(a)).toFloat()
            miniCircles.add(MiniCircle(PointF(x, y), if (i == count - 1) color else miniCircles.getOrNull(i)?.color ?: 0xFFFFFFFF.toInt()))
        }

        invalidate()
    }

    /**
     * Allows updating a mini-circle's color by index.
     */
    fun updateMiniCircleColor(index: Int, color: Int) {
        if (index in miniCircles.indices) {
            miniCircles[index].color = color
            invalidate()
        }
    }

    /**
     * Clears all mini-circles.
     */
    fun clearMiniCircles() {
        miniCircles.clear()
        invalidate()
    }
}

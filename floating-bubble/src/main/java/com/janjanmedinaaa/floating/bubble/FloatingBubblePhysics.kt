package com.janjanmedinaaa.floating.bubble

import android.graphics.Point
import android.view.View
import android.view.WindowManager
import com.janjanmedinaaa.floating.bubble.listener.DefaultFloatingBubbleTouchListener

class FloatingBubblePhysics private constructor(builder: Builder) :
    DefaultFloatingBubbleTouchListener() {

    companion object {
        const val DEFAULT_POINT_VALUE = -9999
    }

    private val sizeX: Int
    private val bubbleView: View?

    private val animator: FloatingBubbleAnimator
    private val previous = arrayOf(
        Point(DEFAULT_POINT_VALUE, DEFAULT_POINT_VALUE),
        Point(DEFAULT_POINT_VALUE, DEFAULT_POINT_VALUE)
    )

    init {
        sizeX = builder.sizeX
        bubbleView = builder.bubbleView

        val bubbleParams = bubbleView!!.layoutParams as WindowManager.LayoutParams
        animator = FloatingBubbleAnimator.Builder()
            .bubbleParams(bubbleParams)
            .bubbleView(bubbleView)
            .sizeX(sizeX)
            .sizeY(builder.sizeY)
            .windowManager(builder.windowManager)
            .build()
    }

    override fun onDown(x: Float, y: Float) {
        super.onDown(x, y)
        previous[0] = Point(DEFAULT_POINT_VALUE, DEFAULT_POINT_VALUE)
        previous[1] = Point(x.toInt(), y.toInt())
    }

    override fun onMove(x: Float, y: Float) {
        super.onMove(x, y)
        addSelectively(x, y)
    }

    override fun onUp(x: Float, y: Float) {
        addSelectively(x, y)

        if (previous[0].isNull()) moveToCorner() else moveLinearlyToCorner()
    }

    private fun moveLinearlyToCorner() {
        val x1 = previous[0].x
        val y1 = previous[0].y
        val x2 = previous[1].x
        val y2 = previous[1].y

        if (x2 == x1) {
            moveToCorner()
            return
        }

        val xf = if (x1 < x2) sizeX - bubbleView!!.width else 0
        val yf = y1 + (y2 - y1) * (xf - x1) / (x2 - x1)
        animator.animate(xf.toFloat(), yf.toFloat())
    }

    private fun moveToCorner() {
        if (previous[1].x < sizeX / 2) {
            animator.animate(0f, previous[1].y.toFloat())
        } else {
            animator.animate(
                (sizeX - bubbleView!!.width).toFloat(),
                previous[1].y.toFloat()
            )
        }
    }

    private fun addSelectively(x: Float, y: Float) {
        if (!previous[1].isNull() && previous[1].x == x.toInt() && previous[1].y == y.toInt()) {
            return
        }

        previous[0] = previous[1]
        previous[1] = Point(x.toInt(), y.toInt())
    }

    private fun Point.isNull() = x == DEFAULT_POINT_VALUE && y == DEFAULT_POINT_VALUE

    class Builder internal constructor() {
        var sizeX: Int = 0
        var sizeY: Int = 0
        var bubbleView: View? = null
        var windowManager: WindowManager? = null
        private var config: FloatingBubbleConfig? = null

        fun sizeX(value: Int) = apply { sizeX = value }

        fun sizeY(value: Int) = apply { sizeY = value }

        fun bubbleView(value: View) = apply { bubbleView = value }

        fun windowManager(value: WindowManager) = apply { windowManager = value }

        fun config(value: FloatingBubbleConfig) = apply { config = value }

        fun build() = FloatingBubblePhysics(this)
    }
}

package com.janjanmedinaaa.floating.bubble

import android.animation.ValueAnimator
import android.view.View
import android.view.WindowManager

internal class FloatingBubbleAnimator private constructor(builder: Builder) {

    companion object {
        const val ANIMATION_TIME = 100
        const val ANIMATION_STEPS = 5
    }

    private val bubbleView: View?
    private val bubbleParams: WindowManager.LayoutParams?
    private val windowManager: WindowManager?
    private val sizeX: Int
    private val sizeY: Int

    init {
        bubbleView = builder.bubbleView
        bubbleParams = builder.bubbleParams
        windowManager = builder.windowManager
        sizeX = builder.sizeX
        sizeY = builder.sizeY
    }

    fun animate(x: Float, y: Float) {
        val startX = bubbleParams!!.x.toFloat()
        val startY = bubbleParams.y.toFloat()
        val animator = ValueAnimator.ofInt(0, 5)

        animator.duration = ANIMATION_TIME.toLong()
        animator.addUpdateListener { valueAnimator ->
            try {
                val currentX =
                    startX + (x - startX) * valueAnimator.animatedValue as Int / ANIMATION_STEPS
                val currentY =
                    startY + (y - startY) * valueAnimator.animatedValue as Int / ANIMATION_STEPS
                bubbleParams.x = currentX.toInt()
                bubbleParams.x = if (bubbleParams.x < 0) 0 else bubbleParams.x
                bubbleParams.x =
                    if (bubbleParams.x > sizeX - bubbleView!!.width) sizeX - bubbleView.width else bubbleParams.x

                bubbleParams.y = currentY.toInt()
                bubbleParams.y = if (bubbleParams.y < 0) 0 else bubbleParams.y
                bubbleParams.y =
                    if (bubbleParams.y > sizeY - bubbleView.width) sizeY - bubbleView.width else bubbleParams.y

                windowManager!!.updateViewLayout(bubbleView, bubbleParams)
            } catch (exception: Exception) {
            }
        }
        animator.start()
    }

    class Builder internal constructor() {
        var bubbleView: View? = null
        var bubbleParams: WindowManager.LayoutParams? = null
        var windowManager: WindowManager? = null
        var sizeX: Int = 0
        var sizeY: Int = 0

        fun bubbleView(value: View) = apply { bubbleView = value }

        fun bubbleParams(value: WindowManager.LayoutParams) = apply {
            bubbleParams = value
        }

        fun windowManager(value: WindowManager?) = apply { windowManager = value }

        fun sizeX(value: Int) = apply { sizeX = value }

        fun sizeY(value: Int) = apply { sizeY = value }

        fun build() = FloatingBubbleAnimator(this)
    }
}

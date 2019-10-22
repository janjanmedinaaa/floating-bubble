package com.janjanmedinaaa.floating.bubble

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleActionListener
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleTouchListener

class FloatingBubbleTouch private constructor(builder: Builder) : View.OnTouchListener {

    companion object {
        const val TOUCH_CLICK_TIME = 250
        const val EXPANSION_FACTOR = 1.25f
    }

    private val sizeX: Int
    private val sizeY: Int

    private val bubbleView: View?
    private val removeBubbleView: View?
    private val windowManager: WindowManager?
    private val listener: FloatingBubbleTouchListener?
    private val physics: FloatingBubbleTouchListener?
    private val actionListener: FloatingBubbleActionListener
    private val marginBottom: Int

    private val bubbleParams: WindowManager.LayoutParams
    private val removeBubbleParams: WindowManager.LayoutParams
    private val removeBubbleStartSize: Int
    private val removeBubbleExpandedSize: Int
    private val animator: FloatingBubbleAnimator

    private var touchStartTime: Long = 0

    private val isInsideRemoveBubble: Boolean
        get() {
            val bubbleSize = if (removeBubbleView!!.width == 0)
                removeBubbleStartSize
            else
                removeBubbleView.width
            val top = removeBubbleParams.y
            val right = removeBubbleParams.x + bubbleSize
            val bottom = removeBubbleParams.y + bubbleSize
            val left = removeBubbleParams.x

            val centerX = bubbleParams.x + bubbleView!!.width / 2
            val centerY = bubbleParams.y + bubbleView.width / 2

            return centerX in (left + 1) until right && centerY > top && centerY < bottom
        }

    init {
        val removeBubbleSize = builder.removeBubbleSize
        physics = builder.physics
        listener = builder.listener
        windowManager = builder.windowManager
        removeBubbleView = builder.removeBubbleView
        bubbleView = builder.bubbleView
        sizeY = builder.sizeY
        sizeX = builder.sizeX
        marginBottom = builder.marginBottom

        actionListener = builder.config!!.actionListener!!
        bubbleParams = bubbleView!!.layoutParams as WindowManager.LayoutParams
        removeBubbleParams = removeBubbleView!!.layoutParams as WindowManager.LayoutParams
        removeBubbleStartSize = removeBubbleSize
        removeBubbleExpandedSize = (EXPANSION_FACTOR * removeBubbleSize).toInt()
        animator = FloatingBubbleAnimator.Builder()
            .sizeX(sizeX)
            .sizeY(sizeY)
            .windowManager(windowManager)
            .bubbleView(bubbleView)
            .bubbleParams(bubbleParams)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        var lastTouchTime = System.currentTimeMillis()

        when (motionEvent.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchStartTime = System.currentTimeMillis()
                showRemoveBubble(View.VISIBLE)
                listener?.onDown(motionEvent.rawX, motionEvent.rawY)
                physics!!.onDown(motionEvent.rawX, motionEvent.rawY)
                actionListener.onBubbleViewClicked()
            }

            MotionEvent.ACTION_MOVE -> {
                moveBubbleView(motionEvent)
                if (lastTouchTime - touchStartTime > TOUCH_CLICK_TIME) {
                    showRemoveBubble(View.VISIBLE)
                }

                listener?.onMove(motionEvent.rawX, motionEvent.rawY)
                physics!!.onMove(motionEvent.rawX, motionEvent.rawY)
                actionListener.onBubbleViewMoved(motionEvent.rawX, motionEvent.rawY)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                showRemoveBubble(View.GONE)
                lastTouchTime = System.currentTimeMillis()
                if (lastTouchTime - touchStartTime >= TOUCH_CLICK_TIME) {
                    val isRemoved = checkRemoveBubble(
                        motionEvent.rawX,
                        motionEvent.rawY
                    )
                    listener?.onUp(motionEvent.rawX, motionEvent.rawY)
                    if (!isRemoved) {
                        physics!!.onUp(motionEvent.rawX, motionEvent.rawY)
                    } else {
                        actionListener.onDragToRemove()
                    }
                }
            }
        }
        return true
    }

    private fun moveBubbleView(motionEvent: MotionEvent) {
        val halfClipSize = (bubbleView!!.width / 2).toFloat()
        val clipSize = bubbleView.width.toFloat()

        var leftX = motionEvent.rawX - halfClipSize
        leftX = if (leftX > sizeX - clipSize) sizeX - clipSize else leftX
        leftX = if (leftX < 0) 0f else leftX

        var topY = motionEvent.rawY - halfClipSize
        topY = if (topY > sizeY - clipSize) sizeY - clipSize else topY
        topY = if (topY < 0) 0f else topY

        bubbleParams.x = leftX.toInt()
        bubbleParams.y = topY.toInt()

        handleRemove()
        windowManager!!.updateViewLayout(bubbleView, bubbleParams)
        windowManager.updateViewLayout(removeBubbleView, removeBubbleParams)
    }

    private fun handleRemove() {
        removeBubbleParams.run {
            height = if (isInsideRemoveBubble) removeBubbleExpandedSize else removeBubbleStartSize
            width = if (isInsideRemoveBubble) removeBubbleExpandedSize else removeBubbleStartSize

            x = (sizeX - width) / 2
            y = sizeY - height - marginBottom

            if (isInsideRemoveBubble) {
                bubbleParams.x = x + (removeBubbleExpandedSize - bubbleView!!.width) / 2
                bubbleParams.y = y + (removeBubbleExpandedSize - bubbleView.width) / 2
            }
        }
    }

    private fun checkRemoveBubble(x: Float, y: Float): Boolean {
        if (isInsideRemoveBubble) {
            physics!!.onUp(x, y)
            expandView(x, y)
            listener?.onTap(false)
            physics.onTap(false)
        }

        return isInsideRemoveBubble
    }

    private fun showRemoveBubble(visibility: Int) {
        removeBubbleView!!.visibility = visibility
    }

    private fun expandView(x: Float, y: Float) {
        physics!!.onUp(x, y)
        actionListener.onExpandedView()
    }

    class Builder internal constructor() {
        var sizeX: Int = 0
        var sizeY: Int = 0
        var bubbleView: View? = null
        var removeBubbleView: View? = null
        var windowManager: WindowManager? = null
        var listener: FloatingBubbleTouchListener? = null
        var removeBubbleSize: Int = 0
        var physics: FloatingBubbleTouchListener? = null
        var config: FloatingBubbleConfig? = null
        var marginBottom: Int = 0

        fun sizeX(value: Int) = apply { sizeX = value }

        fun sizeY(value: Int) = apply { sizeY = value }

        fun bubbleView(value: View) = apply { bubbleView = value }

        fun removeBubbleView(value: View) = apply { removeBubbleView = value }

        fun windowManager(value: WindowManager) = apply { windowManager = value }

        fun build() = FloatingBubbleTouch(this)

        fun removeBubbleSize(value: Int) = apply { removeBubbleSize = value }

        fun physics(value: FloatingBubbleTouchListener) = apply { physics = value }

        fun listener(value: FloatingBubbleTouchListener) = apply { listener = value }

        fun config(value: FloatingBubbleConfig) = apply { config = value }

        fun marginBottom(value: Int) = apply { marginBottom = value }
    }
}

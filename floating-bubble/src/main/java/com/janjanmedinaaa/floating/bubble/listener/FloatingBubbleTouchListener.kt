package com.janjanmedinaaa.floating.bubble.listener

interface FloatingBubbleTouchListener {
    fun onDown(x: Float, y: Float)
    fun onTap(expanded: Boolean)
    fun onRemove()
    fun onMove(x: Float, y: Float)
    fun onUp(x: Float, y: Float)
}

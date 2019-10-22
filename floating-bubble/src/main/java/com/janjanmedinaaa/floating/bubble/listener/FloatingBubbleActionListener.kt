package com.janjanmedinaaa.floating.bubble.listener

interface FloatingBubbleActionListener {
    fun onExpandedView()
    fun onDragToRemove()
    fun onBubbleViewClicked()
    fun onBubbleViewCreated()
    fun onBubbleViewClosed()
    fun onBubbleViewMoved(x: Float, y: Float)
}

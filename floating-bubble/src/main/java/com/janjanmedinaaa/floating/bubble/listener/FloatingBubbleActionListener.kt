package com.janjanmedinaaa.floating.bubble.listener

import android.view.View
import android.view.WindowManager

interface FloatingBubbleActionListener {
    fun onExpandedView() {}
    fun onDragToRemove(bubbleView: View, bubbleParams: WindowManager.LayoutParams) {}
    fun onBubbleViewClicked() {}
    fun onBubbleViewCreated() {}
    fun onBubbleViewClosed() {}
    fun onBubbleViewMoved(bubbleView: View, bubbleParams: WindowManager.LayoutParams) {}
}

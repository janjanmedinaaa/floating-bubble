package com.janjanmedinaaa.floating.bubble

import android.annotation.SuppressLint
import android.content.Intent
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleActionListener

class BubbleService : FloatingBubbleService(), FloatingBubbleActionListener {

    @SuppressLint("InflateParams")
    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleView(R.layout.layout_bubble_view)
            .removeBubbleView(R.layout.layout_remove_bubble_view)
            .bubbleViewSize(64)
            .removeBubbleViewSize(64)
            .onActionListener(this)
            .build()
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true
    }

    override fun onExpandedView() {}

    override fun onBubbleViewCreated() {}

    override fun onBubbleViewClosed() {}

    override fun onDragToRemove() {
        removeAllViews()
    }

    override fun onBubbleViewClicked() {}

    override fun onBubbleViewMoved(x: Float, y: Float) {}
}
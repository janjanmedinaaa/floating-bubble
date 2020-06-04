package com.janjanmedinaaa.floating.bubble

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.WindowManager
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleActionListener

class BubbleService : FloatingBubbleService(), FloatingBubbleActionListener {

    @SuppressLint("InflateParams")
    override fun getConfig(): FloatingBubbleConfig {
        return FloatingBubbleConfig.Builder()
            .bubbleView(R.layout.layout_bubble_view)
            .removeBubbleView(R.layout.layout_remove_bubble_view)
            .bubbleViewSize(70)
            .removeBubbleViewSize(70)
            .onActionListener(this)
            .build()
    }

    override fun onGetIntent(intent: Intent): Boolean {
        return true
    }

    override fun onDragToRemove(
        bubbleView: View,
        bubbleParams: WindowManager.LayoutParams
    ) {
        removeAllViews()
    }

    override fun onBubbleViewClicked() {}
}
package com.janjanmedinaaa.floating.bubble

import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleActionListener

class FloatingBubbleConfig private constructor(builder: Builder) {

    companion object {
        private val defaultBuilder: Builder
            get() = Builder()
                .bubbleView(R.layout.layout_bubble_view)
                .removeBubbleView(R.layout.layout_remove_bubble_view)
                .bubbleViewSize(64)
                .removeBubbleViewSize(64)

        internal val default: FloatingBubbleConfig
            get() = defaultBuilder.build()
    }

    internal val bubbleView: Int
    internal val removeBubbleView: Int
    internal val bubbleViewSize: Int
    internal val removeBubbleViewSize: Int
    internal val actionListener: FloatingBubbleActionListener?

    init {
        bubbleView = builder.bubbleView
        removeBubbleView = builder.removeBubbleView
        bubbleViewSize = builder.bubbleViewSize
        removeBubbleViewSize = builder.removeBubbleViewSize
        actionListener = builder.actionListener
    }

    class Builder {
        internal var bubbleView: Int = 0
        internal var removeBubbleView: Int = 0
        internal var bubbleViewSize = 64
        internal var removeBubbleViewSize = 64
        internal var actionListener: FloatingBubbleActionListener? = null

        fun bubbleView(value: Int) = apply { bubbleView = value }

        fun removeBubbleView(value: Int) = apply { removeBubbleView = value }

        fun bubbleViewSize(value: Int) = apply { bubbleViewSize = value }

        fun removeBubbleViewSize(value: Int) = apply { removeBubbleViewSize = value }

        fun onActionListener(value: FloatingBubbleActionListener) = apply {
            actionListener = value
        }

        fun build() = FloatingBubbleConfig(this)
    }
}

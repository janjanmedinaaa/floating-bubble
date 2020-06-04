package com.janjanmedinaaa.floating.bubble

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics.DENSITY_DEFAULT
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import com.janjanmedinaaa.floating.bubble.listener.DefaultFloatingBubbleTouchListener
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleActionListener
import com.janjanmedinaaa.floating.bubble.listener.FloatingBubbleTouchListener
import kotlin.math.roundToInt

open class FloatingBubbleService : Service() {

    private var windowManager: WindowManager? = null
    private lateinit var inflater: LayoutInflater
    private var windowSize = Point()

    private var bubbleView: View? = null
    private var removeBubbleView: View? = null
    private var expandableView: View? = null

    private lateinit var bubbleParams: WindowManager.LayoutParams
    private lateinit var removeBubbleParams: WindowManager.LayoutParams

    private var config: FloatingBubbleConfig? = null
    private var actionListener: FloatingBubbleActionListener? = null

    private val touchListener: FloatingBubbleTouchListener
        get() = object : DefaultFloatingBubbleTouchListener() {
            override fun onRemove() {
                stopSelf()
            }
        }

    private val expandableViewBottomMargin: Int
        get() {
            val resources = applicationContext.resources
            val resourceId = resources.getIdentifier(
                "navigation_bar_height",
                "dimen",
                "android"
            )
            var navBarHeight = 0
            if (resourceId > 0) {
                navBarHeight = resources.getDimensionPixelSize(resourceId)
            }

            return navBarHeight
        }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || !onGetIntent(intent)) {
            return START_NOT_STICKY
        }

        removeAllViews()
        setupWindowManager()
        setupViews()
        setTouchListener()
        return super.onStartCommand(intent, flags, START_STICKY)

    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllViews()
        actionListener!!.onBubbleViewClosed()
    }

    fun removeAllViews() {
        if (windowManager == null) return

        bubbleView?.let {
            windowManager!!.removeView(it)
            bubbleView = null
        }

        removeBubbleView?.let {
            windowManager!!.removeView(it)
            removeBubbleView = null
        }

        expandableView?.let {
            windowManager!!.removeView(it)
            expandableView = null
        }
    }

    private fun setupWindowManager() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        setLayoutInflater()
        windowManager!!.defaultDisplay.getSize(windowSize)
    }

    fun getWindowSize() = windowSize

    fun getWindowManager() = windowManager

    private fun setLayoutInflater() {
        inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    private fun setupViews() {
        config = getConfig()
        val bubbleViewSize = config!!.bubbleViewSize.dpToPixels()
        val removeBubbleViewSize = config!!.removeBubbleViewSize.dpToPixels()
        val bottomMargin = expandableViewBottomMargin

        actionListener = config!!.actionListener

        bubbleView = inflater.inflate(config!!.bubbleView, null)
        removeBubbleView = inflater.inflate(config!!.removeBubbleView, null)

        removeBubbleParams = getDefaultWindowParams()
        removeBubbleParams.run {
            gravity = Gravity.TOP or Gravity.START
            width = removeBubbleViewSize
            height = removeBubbleViewSize
            x = (windowSize.x - width) / 2
            y = windowSize.y - height - bottomMargin
            removeBubbleView!!.visibility = View.GONE
        }
        windowManager!!.addView(removeBubbleView, removeBubbleParams)

        bubbleParams = getDefaultWindowParams()
        bubbleParams.run {
            gravity = Gravity.TOP or Gravity.START
            width = bubbleViewSize
            height = bubbleViewSize
        }
        windowManager!!.addView(bubbleView, bubbleParams)

        actionListener!!.onBubbleViewCreated()
    }

    private fun setTouchListener() {
        val physics = FloatingBubblePhysics.Builder()
            .sizeX(windowSize.x)
            .sizeY(windowSize.y)
            .bubbleView(bubbleView!!)
            .config(config!!)
            .windowManager(windowManager!!)
            .build()

        val touch = removeBubbleView?.let {
            FloatingBubbleTouch.Builder()
                .sizeX(windowSize.x)
                .sizeY(windowSize.y)
                .listener(touchListener)
                .physics(physics)
                .bubbleView(bubbleView!!)
                .removeBubbleSize(config!!.removeBubbleViewSize.dpToPixels())
                .windowManager(windowManager!!)
                .removeBubbleView(it)
                .config(config!!)
                .marginBottom(expandableViewBottomMargin)
                .build()
        }

        bubbleView!!.setOnTouchListener(touch)
    }

    protected open fun getConfig() = FloatingBubbleConfig.default

    @Suppress("DEPRECATION")
    private fun getDefaultWindowParams(
        width: Int = WRAP_CONTENT,
        height: Int = WRAP_CONTENT
    ): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            width,
            height,
            if (Build.VERSION.SDK_INT >= 26)
                TYPE_APPLICATION_OVERLAY
            else
                TYPE_PHONE,
            FLAG_NOT_FOCUSABLE or FLAG_WATCH_OUTSIDE_TOUCH or FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )
    }

    protected open fun onGetIntent(intent: Intent) = true

    private fun Int.dpToPixels(): Int {
        val displayMetrics = resources.displayMetrics
        return (this * (displayMetrics.densityDpi / DENSITY_DEFAULT)).toFloat()
            .roundToInt()
    }
}

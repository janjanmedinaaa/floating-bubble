package com.janjanmedinaaa.floating.bubble

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBubblePermissionRequest(this)
    }

    override fun onResume() {
        super.onResume()

        if (!requiresPermission(this)) {
            startService(Intent(this, BubbleService::class.java))
        }
    }
}

package com.janjanmedinaaa.floating.bubble

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

private fun requiresPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)
}

fun startBubblePermissionRequest(activity: Activity) {
    if (Build.VERSION.SDK_INT >= 23 && requiresPermission(activity)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + activity.packageName)
        )
        activity.startActivityForResult(intent, 1201)
    }
}

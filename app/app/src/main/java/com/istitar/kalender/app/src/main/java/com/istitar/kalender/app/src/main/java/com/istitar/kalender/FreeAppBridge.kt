package com.istitar.kalender

import android.content.Context
import android.webkit.JavascriptInterface

class FreeAppBridge(private val context: Context) {
    @JavascriptInterface
    fun triggerUpdate() {
        IstiwakForegroundService.startOrUpdate(context)
    }
}

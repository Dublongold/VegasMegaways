package com.example.vegasmegaways.activities.webActivitty

import android.webkit.WebView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainWebActivityCallbacks @Inject constructor() {
    lateinit var setSomeProperties: (WebView, Boolean) -> Unit
}
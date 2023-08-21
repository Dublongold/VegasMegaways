package com.example.vegasmegaways

import android.app.Application
import com.example.vegasmegaways.activities.webActivitty.MainWebActivityCallbacks
import com.example.vegasmegaways.singletons.UrlForWebView
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication: Application() {
    @Inject lateinit var urlForWebView: UrlForWebView
    @Inject lateinit var mainWebActivityCallbacks: MainWebActivityCallbacks
}
package com.example.vegasmegaways.activities.webActivitty

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher

private val regex = Regex("@")

fun processNewUrl(requestUri: String): String {
    val nu1 = requestUri.split(regex)
    val nu2 = nu1.dropLastWhile { it.isEmpty() }
    val nu3 = nu2.toTypedArray()[1].split("#Inten".toRegex())
    return nu3.dropLastWhile { it.isEmpty() } .toTypedArray()[0]
}

fun setOnBack(onBackPressedDispatcher: OnBackPressedDispatcher, theMainWebView: WebView) {
    onBackPressedDispatcher.addCallback(
        object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (theMainWebView.canGoBack()) {
                    theMainWebView.goBack()
                }
            }
        }
    )
}

fun WebView.getUserAgent() = settings.userAgentString
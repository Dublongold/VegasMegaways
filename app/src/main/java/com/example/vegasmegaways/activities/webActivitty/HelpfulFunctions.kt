package com.example.vegasmegaways.activities.webActivitty

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

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

fun WebView.getUserAgentString() = settings.userAgentString

/** Get register for activity result */
fun MainWebActivity.getrFAR(setUri: (Uri) -> Unit) = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { _: Boolean? ->
    lifecycleScope.launch(Dispatchers.IO) {
        lateinit var photoFile: File
        try {
            photoFile = createFile()
        } catch (_: IOException) {}
        val uriFromFile = Uri.fromFile(photoFile)
        val tpiArr = arrayOf(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        tpiArr[0].putExtra(MediaStore.EXTRA_OUTPUT, uriFromFile)
        setUri(uriFromFile)

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        setChooserIntent(chooserIntent, tpiArr)
        startActivityForResult(chooserIntent, 1)
    }
}
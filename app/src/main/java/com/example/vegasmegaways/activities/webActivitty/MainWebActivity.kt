package com.example.vegasmegaways.activities.webActivitty

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.vegasmegaways.R
import com.example.vegasmegaways.singletons.UrlForWebView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainWebActivity: AppCompatActivity() {
    @Inject lateinit var url: UrlForWebView
    private lateinit var theMainWebView: WebView
    /** File path callback */
    private var fpCallback: ValueCallback<Array<Uri>>? = null
    /** Callback */
    private var cb: Uri? = null
    @Inject lateinit var callbacks: MainWebActivityCallbacks
    private val webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            getrFAR {
                cb = it
            }.launch(Manifest.permission.CAMERA)
            fpCallback = filePathCallback
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_web_view)
        theMainWebView = findViewById(R.id.theWebView)
        setOnBack(onBackPressedDispatcher, theMainWebView)
        conf()
        theMainWebView.loadUrl(url.url)
    }

    private fun conf() {
        theMainWebView.let {
            /** Default value for some webView's settings properties.*/
            val defaultVal = true
            callbacks.setSomeProperties(it, defaultVal)
            it.settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            it.settings.cacheMode = WebSettings.LOAD_DEFAULT
            /** User agent. */
            val ua = it.getUserAgentString()
            it.settings.userAgentString = ua.replace("; wv", "")
        }
        confOther()
    }

    /** Configure other web view settings. */
    private fun confOther() {
        theMainWebView.let {
            it.webChromeClient = webChromeClient
            it.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    view.loadUrl(request.url.toString())
                    return true
                }
            }
            val cookieManager = CookieManager.getInstance()
            cookieManager.let {cm ->
                cm.setAcceptCookie(true)
                cm.setAcceptThirdPartyCookies(it, true)
            }
        }
    }

    fun createFile() = File.createTempFile(FILE_PREFIX, FILE_FORMAT, getExternalFilesDir(Environment.DIRECTORY_PICTURES))

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fpCallback?.let {
            it.onReceiveValue(if (resultCode != -1) null else onReceiveValueCheck(data))
            fpCallback = null
        }
    }
    private fun onReceiveValueCheck(data: Intent?): Array<Uri>? {
        data?.run {
            val d = dataString
            if (d != null) {
                return arrayOf(Uri.parse(d))
            }
        }
        return checkCb()
    }
    private fun checkCb(): Array<Uri>? {
        val cb = cb
        return if(cb == null) null else arrayOf(cb)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        theMainWebView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        theMainWebView.restoreState(savedInstanceState)
    }

    fun setChooserIntent(intent: Intent, intentArr: Array<Intent>) {
        intent.putExtra(Intent.EXTRA_INTENT, Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = ACTION_GET_CONTENT_TYPE
        })

        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArr)
    }

    companion object {
        private const val ACTION_GET_CONTENT_TYPE = "*/*"
        private const val FILE_PREFIX = "file"
        private const val FILE_FORMAT = ".jpg"
    }
}
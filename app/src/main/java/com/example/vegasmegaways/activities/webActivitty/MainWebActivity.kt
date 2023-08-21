package com.example.vegasmegaways.activities.webActivitty

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vegasmegaways.R
import com.example.vegasmegaways.singletons.UrlForWebView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainWebActivity: AppCompatActivity() {
    @Inject lateinit var url: UrlForWebView
    private lateinit var theMainWebView: WebView
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var cb: Uri? = null
    @Inject lateinit var callbacks: MainWebActivityCallbacks
    private val webChromeClient = object : WebChromeClient() {
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams
        ): Boolean {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            mFilePathCallback = filePathCallback
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_web_view)
        theMainWebView = findViewById(R.id.theWebView)
        setOnBack()
        conf()
    }

    private fun setOnBack() {
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

    private fun conf() {
        theMainWebView.let {
            val defaultVal = true
            callbacks.setSomeProperties.invoke(it, defaultVal)
            it.settings.mixedContentMode = 0
            it.settings.cacheMode = WebSettings.LOAD_DEFAULT
            val usrAgent = it.settings.userAgentString
            it.settings.userAgentString = usrAgent.replace("; wv", "")
        }
        confOther()
    }

    private fun confOther() {
        theMainWebView.let {
            it.webChromeClient = webChromeClient
            it.webViewClient = client()
            it.loadUrl(url.url)
            val cookieManager = CookieManager.getInstance()
            cookieManager.let {cm ->
                cm.setAcceptCookie(true)
                cm.setAcceptThirdPartyCookies(it, true)
            }
        }
    }

    private val client = {
        object : WebViewClient() {
            var content: Boolean = false
            var method: String? = null
            var startActionView = false
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val requestUri = request.url.toString()
                if(!requestUri.contains("/")) return true
                val c1 = requestUri.contains("intent://ti/p/")
                val c2 = requestUri.contains("#")
                return if (c1.and(c2)) {
                    var nu = "line://ti/p/@"
                    nu += processNewUrl(requestUri)
                    startActionView = true
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(nu)))
                    startActionView
                } else {
                    if (requestUri.contains("http")) {
                        content
                    } else {
                        startActionView = true
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(requestUri)))
                        startActionView
                    }
                }
            }

            override fun onReceivedLoginRequest(
                view: WebView,
                realm: String,
                account: String?,
                args: String
            ) {
                method = ON_RECEIVED_LOGIN_REQUEST
                super.onReceivedLoginRequest(view, realm, account, args)
            }
        }
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean? ->
        lifecycleScope.launch(Dispatchers.IO) {
            lateinit var photoFile: File
            try {
                photoFile = createFile()
            } catch (_: IOException) {}
            val uriFromFile = Uri.fromFile(photoFile)
            val tpi = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            tpi.putExtra(MediaStore.EXTRA_OUTPUT, uriFromFile)
            cb = uriFromFile

            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            setChooserIntent(chooserIntent, arrayOf(tpi))
            startActivityForResult(chooserIntent, 1)
        }
    }
    private fun createFile() = File.createTempFile(FILE_PREFIX, FILE_FORMAT, getExternalFilesDir(Environment.DIRECTORY_PICTURES))

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFilePathCallback?.let {
            if (resultCode == -1) {
                it.onReceiveValue(onReceiveValueCheck(data))
            }
            else
                it.onReceiveValue(null)
            mFilePathCallback = null
        }
    }
    private fun onReceiveValueCheck(data: Intent?): Array<Uri>? {
        if (data != null) {
            val d = data.dataString
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

    private fun setChooserIntent(intent: Intent, intentArr: Array<Intent>) {
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
        private const val ON_RECEIVED_LOGIN_REQUEST = "OnReceivedLoginReq"
    }
}
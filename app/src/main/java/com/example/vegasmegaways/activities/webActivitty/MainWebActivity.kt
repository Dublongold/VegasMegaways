package com.example.vegasmegaways.activities.webActivitty

import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.annotation.SuppressLint
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
import com.example.vegasmegaways.R
import com.example.vegasmegaways.singletons.UrlForWebView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainWebActivity: AppCompatActivity() {
    @Inject lateinit var url: UrlForWebView
    private lateinit var theMainWebView: WebView
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var calback: Uri? = null
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
            it.settings.allowContentAccess = true
            it.settings.allowFileAccessFromFileURLs = true
            it.settings.mixedContentMode = 0
            it.settings.cacheMode = WebSettings.LOAD_DEFAULT
            val usrAgent = it.settings.userAgentString
            it.settings.setUserAgentString(usrAgent.replace("; wv", ""))
            // we!!.settings.setAppCacheEnabled(true)
            it.settings.domStorageEnabled = true
            it.settings.databaseEnabled = true
            it.settings.useWideViewPort = true
            it.settings.javaScriptEnabled = true
            it.settings.allowFileAccess = true
            it.settings.javaScriptCanOpenWindowsAutomatically = true
            it.settings.loadWithOverviewMode = true
            it.settings.allowUniversalAccessFromFileURLs = true
        }
        confOther()
    }

    private fun confOther() {
        theMainWebView.let {
            it.webChromeClient = webChromeClient
            it.webViewClient = client()
            it.loadUrl(url.url)
            CookieManager.getInstance().let {cm ->
                cm.setAcceptCookie(true)
                cm.setAcceptThirdPartyCookies(it, true)
            }
        }
    }

    private val client = {
        object : WebViewClient() {
            var content: Boolean? = null
            var method: String? = null
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val requestUri = request.url.toString()
                if(!requestUri.contains("/")) return true
                val c1 = requestUri.contains("intent://ti/p/")
                val c2 = requestUri.contains("#")
                return if (c1.and(c2)) {
                    var nu = "line://ti/p/@"
                    val regex = Regex("@")
                    val nu1 = requestUri.split(regex)
                    val nu2 = nu1.dropLastWhile { it.isEmpty() }
                    val nu3 = nu2.toTypedArray()[1].split("#Inten".toRegex())
                    val nu4 = nu3.dropLastWhile { it.isEmpty() } .toTypedArray()[0]
                    nu += nu4
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(nu)))
                    true
                } else {
                    if (requestUri.contains("http")) {
                        content = false
                        false
                    } else {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(requestUri)))
                        true
                    }
                }
            }

            override fun onReceivedLoginRequest(
                view: WebView,
                realm: String,
                account: String?,
                args: String
            ) {
                method = "OnReceivedLoginReq"
                super.onReceivedLoginRequest(view, realm, account, args)
            }
        }
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->
        val tpi = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        lateinit var photoFile: File
        try {
            photoFile = File.createTempFile(
                "file",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        } catch (_: IOException) {}

        tpi.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Uri.fromFile(photoFile)
        )
        calback = Uri.fromFile(photoFile)
        val old = Intent(Intent.ACTION_GET_CONTENT).also {
           it.addCategory(Intent.CATEGORY_OPENABLE); it.type = "*/*"
        }
        val intentArray = arrayOf(tpi)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.let {
            it.putExtra(Intent.EXTRA_INTENT, old)
            it.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        }
        startActivityForResult(chooserIntent, 1)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFilePathCallback?.let {
            if (resultCode == -1) {
                it.onReceiveValue(if (data != null) {
                    val d = data.dataString
                    if (d != null) {
                        arrayOf(Uri.parse(d))
                    } else {
                        if (calback != null) {
                            arrayOf(calback!!)
                        } else null

                    }
                } else {
                    if (calback != null) arrayOf(calback!!)
                    else null
                })
            }
            else
                it.onReceiveValue(null)
            mFilePathCallback = null
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        theMainWebView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        theMainWebView.restoreState(savedInstanceState)
    }
}
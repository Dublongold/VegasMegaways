package com.example.vegasmegaways.activities.mainActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vegasmegaways.R
import com.example.vegasmegaways.activities.webActivitty.MainWebActivity
import com.example.vegasmegaways.activities.webActivitty.MainWebActivityCallbacks
import com.example.vegasmegaways.fragments.menuFragment.MenuFragment
import com.example.vegasmegaways.fragments.waitingFragment.WaitingFragment
import com.example.vegasmegaways.internet.GetAccess
import com.example.vegasmegaways.singletons.UrlForWebView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var urlForWebView: UrlForWebView
    @Inject lateinit var mainWebActivityCallbacks: MainWebActivityCallbacks
    private lateinit var webIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        supportFragmentManager.beginTransaction()
            .add(R.id.theFragmentContainer, WaitingFragment())
            .commit()

        webIntent = Intent(this, MainWebActivity::class.java)
        mainWebActivityCallbacks.setSomeProperties = { webView, value ->
                webView.settings.allowContentAccess = value
                webView.settings.allowFileAccessFromFileURLs = value
                webView.settings.domStorageEnabled = value
                webView.settings.databaseEnabled = value
                webView.settings.useWideViewPort = value
                webView.settings.javaScriptEnabled = value
                webView.settings.allowFileAccess = value
                webView.settings.javaScriptCanOpenWindowsAutomatically = value
                webView.settings.loadWithOverviewMode = value
                webView.settings.allowUniversalAccessFromFileURLs = value
            }
        fun startWebActivity(url: String) {
            urlForWebView.url = url
            startActivity(webIntent)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val spu = SharedPreferencesUrl(::getSharedPreferences)
            val url = spu.url
            if(url.isNullOrEmpty()) {
                GetAccess.get(this@MainActivity, GetAccess.URL) { pusk, link ->
                    if (pusk && link != null) {
                        spu.url = link
                        startWebActivity(link)
                    } else {
                        supportFragmentManager.beginTransaction()
                            .add(R.id.theFragmentContainer, MenuFragment())
                            .commitAllowingStateLoss()
                        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                val first = supportFragmentManager.fragments.first()
                                if(first is WaitingFragment || first is MenuFragment) {
                                    finish()
                                }
                                else {
                                    supportFragmentManager.beginTransaction()
                                        .replace(R.id.theFragmentContainer, MenuFragment())
                                        .commitAllowingStateLoss()
                                }
                            }
                        })
                    }
                }
            }
            else startWebActivity(url)
        }
    }
    class SharedPreferencesUrl(private val getSharedPreferences: (String, Int) -> SharedPreferences) {
        private val filename = "connection_url"
        private val propertyName = "url"
        private val modePrivate = AppCompatActivity.MODE_PRIVATE

        var url
            get() = getSharedPreferences(filename, modePrivate).getString(propertyName, "")
            set(value) = getSharedPreferences(filename, modePrivate).edit().putString(propertyName, value).apply()
    }
}
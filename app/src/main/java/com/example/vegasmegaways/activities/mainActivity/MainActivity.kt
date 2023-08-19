package com.example.vegasmegaways.activities.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.vegasmegaways.R
import com.example.vegasmegaways.activities.webActivitty.MainWebActivity
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
    private lateinit var webIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_screen)

        supportFragmentManager.beginTransaction()
            .add(R.id.theFragmentContainer, WaitingFragment())
            .commit()

        webIntent = Intent(this, MainWebActivity::class.java)
        lifecycleScope.launch(Dispatchers.IO) {
            GetAccess.get(this@MainActivity, GetAccess.URL) { pusk, link ->
                if (pusk && link != null) {
                    urlForWebView.url = link
                    startActivity(webIntent)
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
    }
}
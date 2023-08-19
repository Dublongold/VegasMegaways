package com.example.vegasmegaways.singletons

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlForWebView @Inject constructor() {
    var url: String = ""
}
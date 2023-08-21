package com.example.vegasmegaways.activities.webActivitty

private val regex = Regex("@")

fun processNewUrl(requestUri: String): String {
    val nu1 = requestUri.split(regex)
    val nu2 = nu1.dropLastWhile { it.isEmpty() }
    val nu3 = nu2.toTypedArray()[1].split("#Inten".toRegex())
    return nu3.dropLastWhile { it.isEmpty() } .toTypedArray()[0]
}
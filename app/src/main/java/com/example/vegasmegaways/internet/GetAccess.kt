package com.example.vegasmegaways.internet

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class GetAccess {
    companion object {
        const val URL = "https://gist.githubusercontent.com/lubetel/b0399e72219b149222ab4dbd5105f7c0/raw/025beb00e63d8383504a0b8d97e69310b99e4eb1/gistfile1.txt"
        fun get(context: Context, url: String, callback: (Boolean, String?) -> Unit) {
            val mRequestQueue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    try {
                        callback(response.getBoolean("pusk"), response.getString("link"))
//                        callback(true, "https://google.com")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }) { error ->
                error.printStackTrace()
            }
            mRequestQueue.add(request)
        }
    }
}
package com.example.vegasmegaways.fragments.gameFragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.vegasmegaways.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

@HiltViewModel
class GameViewModel @Inject constructor(): ViewModel() {
    val coins = MutableStateFlow(10000)

    val canTryAgain = MutableStateFlow(false)

    var canTapOnGift = true

    private var giftsCoins = List(6) {getGitsCoins()}

    private fun getGitsCoins(): Int {
        val position = Random.nextInt(0, 100)
        return VALUES[when(position) {
            in 0 until 50 -> 0
            in 50 until 65 -> 1
            in 65 until 75 -> 2
            in 75 until 80 -> 3
            in 80 until 85 -> 4
            in 85 until 90 -> 5
            in 90 until 95 -> 6
            in 95..100 -> 7
            else -> throw IndexOutOfBoundsException()
        }]
    }

    fun tryAgain() {
        canTryAgain.value = false
        giftsCoins = List(6) {getGitsCoins()}
        canTapOnGift = true
    }

    suspend fun getGift(id: Int, gifts: List<ImageView>, texts: List<TextView>, callback: (Int) -> Unit) {
        canTapOnGift = false
        coins.update {
            it - 100
        }
        var t = 0
        repeat(20) {
            gifts[id].offsetLeftAndRight(t)
            t = if (t == 10) -10 else 10
            delay(100)
        }
        gifts[id].offsetLeftAndRight(0)
        for(gift in gifts) {
            gift.visibility = View.GONE
        }
        for((ind, text) in texts.withIndex()) {
            text.visibility = View.VISIBLE
            text.text = giftsCoins[ind].toString()
        }
        texts[id].setBackgroundResource(R.drawable.selected_gift)
        coins.update {it + giftsCoins[id]}
        delay(1500)
        callback(id)
        canTryAgain.value = true
    }

    companion object {
        val VALUES = listOf(0, 10, 100, 1000, 5000, 10000, 25000, 50000 )
    }
}
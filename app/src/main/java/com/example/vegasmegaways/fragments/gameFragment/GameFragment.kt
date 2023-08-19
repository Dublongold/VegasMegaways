package com.example.vegasmegaways.fragments.gameFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.vegasmegaways.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class GameFragment: Fragment() {
    private lateinit var gameViewModel: GameViewModel

    private lateinit var coinsText: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        gameViewModel = ViewModelProvider(requireActivity())[GameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coinsText = view.findViewById(R.id.coinsCountText)
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.coins.collect {
                    coinsText.text = getString(R.string.coins_count, it)
                }
            }
        }

        val gifts: List<ImageView> = listOf(
            view.findViewById(R.id.gift1),
            view.findViewById(R.id.gift2),
            view.findViewById(R.id.gift3),
            view.findViewById(R.id.gift4),
            view.findViewById(R.id.gift5),
            view.findViewById(R.id.gift6))

        val texts: List<TextView> = listOf(
            view.findViewById(R.id.gift1Coins),
            view.findViewById(R.id.gift2Coins),
            view.findViewById(R.id.gift3Coins),
            view.findViewById(R.id.gift4Coins),
            view.findViewById(R.id.gift5Coins),
            view.findViewById(R.id.gift6Coins)
        )
        setRandomImages(gifts)
        for((ind, gift) in gifts.withIndex()) {
            gift.setOnClickListener {
                if(gameViewModel.canTapOnGift) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        gameViewModel.getGift(ind, gifts, texts) {
                            setRandomImages(gifts)
                            for(g in gifts) {
                                g.visibility = View.VISIBLE
                            }
                            for(t in texts) {
                                t.visibility = View.GONE
                            }
                            texts[ind].background = null
                        }
                    }
                }
            }
        }
        view.findViewById<AppCompatButton>(R.id.tryAgainButton).setOnClickListener {
            gameViewModel.tryAgain()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.canTryAgain.collect {
                    view.findViewById<AppCompatButton>(R.id.tryAgainButton).isEnabled = it
                }
            }
        }
    }

    private fun setRandomImages(gifts: List<ImageView>) {
        for(gift in gifts) {
            gift.setImageResource(
                when(Random.nextInt(0,3)) {
                    0 -> R.drawable.gift_1
                    1 -> R.drawable.gift_2
                    2 -> R.drawable.gift_3
                    else -> throw IndexOutOfBoundsException()
                }
            )
        }
    }
}
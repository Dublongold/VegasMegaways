package com.example.vegasmegaways.fragments.menuFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.vegasmegaways.R
import com.example.vegasmegaways.fragments.gameFragment.GameFragment

class MenuFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.menu_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnClickListener {
            MediaPlayer.create(context, R.raw.pig).start()
        }

        view.findViewById<AppCompatButton>(R.id.buttonGame).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.theFragmentContainer, GameFragment()).commit()
        }
    }
}
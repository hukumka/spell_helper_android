package com.example.pfbase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pfbase.databinding.FragmentLevelSeparatorBinding

private const val ARG_LEVEL = "com.example.pfbase.levelSeparatorFragment.ARG_LEVEL"

class LevelSeparatorFragment : Fragment() {
    private var level: Int? = null
    private var binding: FragmentLevelSeparatorBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            level = it.getInt(ARG_LEVEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLevelSeparatorBinding.inflate(inflater, container, false)
        val text = when(level) {
            null -> "No level"
            0 -> "Orisons"
            else -> level.toString()
        }
        binding!!.level.text = text
        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance(level: Int) =
            LevelSeparatorFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LEVEL, level)
                }
            }
    }
}
package com.example.pfbase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pfbase.databinding.FragmentSpellDescriptionBinding


private const val ARG_ID = "com.example.pfbase.SPELL_ID_ARG"

class SpellDescriptionFragment : Fragment() {
    private var _binding: FragmentSpellDescriptionBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var spell: Spell? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            spell = it.getParcelable(ARG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpellDescriptionBinding.inflate(inflater, container, false)
        val view = binding.root
        val spell = spell
        if (spell != null) {
            binding.spellName.text = spell.name
            binding.spellLevel.text = spell.level.toString()
            binding.castingTime.text = spell.casting_time
            binding.components.text = spell.components
            binding.range.text = spell.range
            binding.duration.text = spell.duration
            binding.effect.text = spell.effect
            binding.save.text = spell.save
            binding.resistance.text = if (spell.resistance != true) "no" else "yes"
            binding.description.text = spell.description
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(spell: Spell?) =
            SpellDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ID, spell)
                }
            }
    }
}
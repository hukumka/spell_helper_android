package com.example.pfbase

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pfbase.databinding.FragmentSpellListBinding
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface SpellPerLevelFactory: Parcelable {
    fun instance(level: Int): Fragment
}

@Parcelize
class BasicSpellPerLevelFactory: SpellPerLevelFactory {
    override fun instance(level: Int): Fragment {
        return SpellListPerLevel.newInstance(SpellsPerLevel(level))
    }
}

@Parcelize
class SlotsPerLevelFactory: SpellPerLevelFactory {
    override fun instance(level: Int): Fragment {
        return SpellListPerLevel.newInstance(SlotPerLevel(level))
    }
}

const val SPELL_LIST_FRAGMENT_ID = "com.example.pfbase.SpellListFragment.ARG_ID"

open class SpellListFragment : Fragment() {
    private var _binding: FragmentSpellListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var fragmentFactory: SpellPerLevelFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fragmentFactory = it.getParcelable(SPELL_LIST_FRAGMENT_ID)
        }
    }

    private fun populateList() {
        var transaction = childFragmentManager.beginTransaction()
        for (i in 0..10) {
            fragmentFactory?.let {
                transaction.add(binding.spellList.id, it.instance(i))
            }
        }
        transaction.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpellListBinding.inflate(inflater, container, false)
        if (savedInstanceState == null) {
            populateList()
        }
        return binding.root
    }

    companion object {
        fun newInstance(factory: SpellPerLevelFactory): SpellListFragment {
            val fragment = SpellListFragment()
            fragment.apply {
                arguments = Bundle().apply {
                    putParcelable(SPELL_LIST_FRAGMENT_ID, factory)
                }
            }
            return fragment
        }
    }
}
package com.example.pfbase

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.pfbase.databinding.FragmentSpellItemBinding
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val ARG_SPELL_ITEM = "com.example.pfbase.SPELL_ITEM"
private const val ARG_SPELL_MANAGER = "com.example.pfbase.SPELL_MANAGER"
private const val ARG_ITEM_ROW = "com.example.pfbase.ITEM_ROW"



class SpellItem : Fragment() {
    private var spell: SpellItemDat? = null
    private var callback: SpellListManager? = null
    private var row: Int = 0
    private var binding: FragmentSpellItemBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            spell = it.getParcelable(ARG_SPELL_ITEM)
            callback = it.getParcelable(ARG_SPELL_MANAGER)
            row = it.getInt(ARG_ITEM_ROW)
        }
    }

    fun updateData(spell: SpellItemDat) {
        this.spell = spell
        this.arguments?.putParcelable(ARG_SPELL_ITEM, spell)
        setIcon()
    }

    fun setIcon() {
        val res = if (spell!!.learned) {
            R.drawable.ic_action_remove
        } else {
            R.drawable.ic_action_add
        }
        binding?.imageButton?.setImageResource(res)
    }

    private fun openDescription(view: View) {
        GlobalScope.launch {
            val spell = AppDatabase.getInstance(view.context)
                .spellDao()
                .getSpell(spell!!.spell_id)
            val intent = Intent(context, DisplayMessageActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, spell)
            }
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpellItemBinding.inflate(inflater, container, false)
        binding!!.itemSpellBox.setOnClickListener{x -> openDescription(x)}
        binding!!.itemSpellName.text = spell!!.name
        binding!!.imageButton.setOnClickListener { x ->
            GlobalScope.launch {
                callback!!.spellClick(x.context, spell!!)
            }
        }
        setIcon()
        return binding?.root
    }

    companion object {
        @JvmStatic
        fun newInstance(row: Int, spell: SpellItemDat, loader: SpellListManager) =
            SpellItem().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SPELL_ITEM, spell)
                    putParcelable(ARG_SPELL_MANAGER, loader)
                    putInt(ARG_ITEM_ROW, row)
                }
            }
    }
}
package com.example.pfbase

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.os.Debug
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.parcel.Parcelize
import com.example.pfbase.databinding.FragmentSpellListPerLevelBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val SPELL_PER_LEVEL_ARG = "com.example.pfbase.SPELL_PER_LEVEL_LOADER_ARG"

interface SpellListManager: Parcelable {
    fun levelText(): String {
        return if (level() == 0) {
            "Orisons"
        } else {
            level().toString()
        }
    }

    fun level(): Int
    fun connect(widget: SpellListPerLevel) {}
    fun getSpells(context: Context): LiveData<List<SpellItemDat>>
    suspend fun spellClick(context: Context, spell: SpellItemDat): Unit
}

@Parcelize
class SpellsPerLevel(val level: Int): SpellListManager {
    override fun level(): Int {
        return level
    }

    override fun getSpells(context: Context) =
        AppDatabase.getInstance(context)
            .spellDao().spellsPerLevel(level)

    override suspend fun spellClick(context: Context, spell: SpellItemDat) {
        if (spell.learned) {
            AppDatabase.getInstance(context)
                    .spellDao()
                    .expendSpell(spell.spell_id)
        } else {
            AppDatabase.getInstance(context)
                    .spellDao()
                    .insertSpellSlot(SpellSlot(spell.spell_id))
        }
    }
}

@Parcelize
class SlotPerLevel(val level: Int): SpellListManager {
    var widget: SpellListPerLevel? = null

    override fun level(): Int {
        return level
    }

    override fun connect(widget: SpellListPerLevel) {
        this.widget = widget
    }

    override fun getSpells(context: Context) =
        AppDatabase.getInstance(context)
            .spellDao().slotsPerLevel(level)

    override suspend fun spellClick(context: Context, spell: SpellItemDat) {
        AppDatabase.getInstance(context)
            .spellDao()
            .expendSpell(spell.spell_id)
    }
}

class SpellListPerLevel : Fragment() {
    private var _loader: SpellListManager? = null
    private val loader get() = _loader!!

    private var _binding: FragmentSpellListPerLevelBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var openedOnce = false
    private var oldSpells: List<SpellItemDat> = emptyList()

    private var lastId = 2_000_000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _loader = it.getParcelable(SPELL_PER_LEVEL_ARG)
            _loader?.connect(this)
        }
    }

    private fun toggle() {
        if (binding.spellItems.visibility == View.VISIBLE) {
            binding.spellItems.visibility = View.GONE;
        } else if (binding.spellItems.childCount != 0){
            binding.spellItems.visibility = View.VISIBLE;
        }
        if (!openedOnce) {
            openedOnce = true
            val context = context
            if (context != null) {
                val observer = loader.getSpells(context)
                observer.observe(viewLifecycleOwner, Observer { spells -> setSpells(spells) })
                binding.spellItems.visibility = View.VISIBLE
            }
        }
    }

    private fun setSpells(spells: List<SpellItemDat>) {
        val context = context ?: return
        val diff = difference(oldSpells, spells) { s -> s.name }
        oldSpells = spells

        for (d in diff.delete.reversed()) {
            binding.spellItems.removeViewAt(d)
        }

        var transaction = childFragmentManager.beginTransaction()
        for (i in diff.insert) {
            val view = FrameLayout(context)
            view.id = lastId
            lastId += 1
            binding.spellItems.addView(view, i)
            val f = SpellItem.newInstance(i, spells[i], loader)
            transaction.add(view.id, f)
        }
        transaction.commit()

        for (u in diff.update) {
            val id = binding.spellItems.get(u).id
            val frag: SpellItem = childFragmentManager.findFragmentById(id) as SpellItem
            frag.updateData(spells[u])
        }

        if (binding.spellItems.childCount == 0) {
            binding.spellItems.visibility = View.GONE;
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpellListPerLevelBinding.inflate(inflater, container, false)
        binding.toggleLevelList.text = loader.levelText()
        binding.toggleLevelList.setOnClickListener{this.toggle()}

        binding.spellItems.visibility = View.GONE;


        return binding.root
    }

    companion object {
        fun newInstance(loader: SpellListManager) =
            SpellListPerLevel().apply {
                arguments = Bundle().apply {
                    putParcelable(SPELL_PER_LEVEL_ARG, loader)
                }
            }
    }
}
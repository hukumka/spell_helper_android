package com.example.pfbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AllSpells : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_spells)

        if(savedInstanceState != null) return
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentContainer, SpellListFragment.newInstance(BasicSpellPerLevelFactory()))
            .commit()
    }
}
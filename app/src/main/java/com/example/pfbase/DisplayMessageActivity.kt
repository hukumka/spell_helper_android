package com.example.pfbase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_message)
        val spell: Spell? = intent.getParcelableExtra(EXTRA_MESSAGE)
        if (savedInstanceState != null) return
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, SpellDescriptionFragment.newInstance(spell))
            .commit()
    }
}
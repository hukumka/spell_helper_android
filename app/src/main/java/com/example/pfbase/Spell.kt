package com.example.pfbase

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(indices = [
    Index(value=["level"], unique = false)
])
@Parcelize
data class Spell(
    @PrimaryKey(autoGenerate = true) var spell_id: Int? = null,
    val name: String = "",
    val level: Int = 0,
    val casting_time: String? = "",
    val components: String? = "",
    val duration: String? = "",
    val range: String? = "",
    val effect: String? = "",
    val save: String? = "",
    val resistance: Boolean? = false,
    val description: String = "",
    val school: String? = ""
) : Parcelable

@Parcelize
data class SpellItemDat(
    val spell_id: Int,
    val name: String = "",
    val learned: Boolean,
) : Parcelable

@Entity
data class SpellSlot(
    @PrimaryKey(autoGenerate = true) var prepared_spell_id: Int?,
)

@Dao
interface SpellDao {
    @Query("SELECT spell.spell_id, spell.name, spellslot.prepared_spell_id IS NOT NULL as learned FROM spell LEFT JOIN spellslot ON spell.spell_id = spellslot.prepared_spell_id WHERE level == :level ORDER BY name")
    fun spellsPerLevel(level: Int): LiveData<List<SpellItemDat>>

    @Query("SELECT spell.spell_id, spell.name, 1 as learned FROM spell INNER JOIN spellslot ON spell.spell_id == spellslot.prepared_spell_id WHERE spell.level == :level ORDER BY name")
    fun slotsPerLevel(level: Int): LiveData<List<SpellItemDat>>

    @Query("DELETE FROM SpellSlot")
    fun deleteSpellSlots()

    @Query("DELETE FROM spellslot WHERE prepared_spell_id == :spellId")
    fun expendSpell(spellId: Int)

    @Query("SELECT * FROM spell WHERE spell_id == :spellId")
    fun getSpell(spellId: Int): Spell?

    @Insert
    fun insertSpellSlot(slots: SpellSlot)

    @Insert
    fun insertSpell(vararg spells: Spell)
}

@Database(entities = [Spell::class, SpellSlot::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun spellDao(): SpellDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "com.example.pfbase.DATABASE"
                    ).createFromAsset("spells.db")
                        .build()
                    INSTANCE = instance
                }
                return instance;
            }
        }
    }
}

package com.example.pfbase

/**
 * deletions - list of indices in `old` list of elements deleted
 * insert - List of indices in `new` of elements added
 * update - List of indices in `new` of elements with the same key, but different value
  */
data class ListDiff (val insert: List<Int>, val delete: List<Int>, val update: List<Int>)

fun<T, K : Comparable<K>> difference(old: List<T>, new: List<T>, key: (T) -> K): ListDiff {
    val delete = mutableListOf<Int>()
    val insert= mutableListOf<Int>()
    val update = mutableListOf<Int>()

    var i = 0
    var j = 0
    while (i < old.size && j < new.size) {
        val oldKey = key(old[i])
        val newKey = key(new[j])
        if (oldKey < newKey) {
            delete.add(i)
            i += 1
        } else if (newKey < oldKey) {
            insert.add(j)
            j += 1
        } else {
            if (old[i] != new[j]) {
                update.add(j)
            }
            i += 1
            j += 1
        }
    }
    while (i < old.size) {
        delete.add(i)
        i += 1
    }
    while (j < new.size) {
        insert.add(j)
        j += 1
    }
    return ListDiff(insert, delete, update)
}
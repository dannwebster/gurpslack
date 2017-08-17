package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.Character
import org.crypticmission.gurpslack.model.toKey
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByAbbrev = HashMap<String, Character>()

    fun add(abbrev: String, characterName: String) : Boolean {
        val key = abbrev.toKey()
        return when (charactersByAbbrev.get(key.toKey())) {
            null -> {
                charactersByAbbrev[key] = Character(characterName, randomizer)
                return true
            }
            else -> false
        }
    }

    fun get(abbrev: String) = charactersByAbbrev[abbrev.toKey()]
    fun remove(abbrev: String) = charactersByAbbrev.remove(abbrev.toKey())
    fun list() = charactersByAbbrev.toList()
}
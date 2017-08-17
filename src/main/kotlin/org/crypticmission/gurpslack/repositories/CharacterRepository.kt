package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.Character
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByAbbrev = HashMap<String, Character>()

    fun key(abbrev: String) = abbrev.trim().toLowerCase()

    fun add(abbrev: String, characterName: String) : Boolean {
        val key = key(abbrev)
        return when (charactersByAbbrev.get(key)) {
            null -> {
                charactersByAbbrev[key] = Character(characterName, randomizer)
                return true
            }
            else -> false
        }
    }

    fun get(abbrev: String) = charactersByAbbrev[key(abbrev)]
    fun remove(abbrev: String) = charactersByAbbrev.remove(key(abbrev))
    fun list() = charactersByAbbrev.toList()
}
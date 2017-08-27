package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.model.toKey
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByAbbrev = HashMap<String, CharacterRoller>()

    fun add(abbrev: String, characterName: String) : Boolean {
        val key = abbrev.toKey()
        return when (charactersByAbbrev.get(key.toKey())) {
            null -> {
                charactersByAbbrev[key] = CharacterRoller(randomizer, characterName)
                return true
            }
            else -> false
        }
    }

    fun put(abbrev: String, character: CharacterRoller) {
        val key = abbrev.toKey()
        charactersByAbbrev[key] = character
    }

    fun get(abbrev: String) = charactersByAbbrev[abbrev.toKey()]
    fun remove(abbrev: String) = charactersByAbbrev.remove(abbrev.toKey())
    fun list() = charactersByAbbrev.toList()
}
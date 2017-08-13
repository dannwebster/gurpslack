package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.Character
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByAbbrev = HashMap<String, Character>()

    fun add(characterName: String, abbrev: String) : Boolean {
        if (charactersByAbbrev.get(abbrev) != null) {
            return false
        } else {
            charactersByAbbrev[abbrev] = Character(characterName, randomizer)
            return true
        }
    }

    fun get(abbrev: String) = charactersByAbbrev[abbrev]
    fun remove(abbrev: String) = charactersByAbbrev.remove(abbrev)
}
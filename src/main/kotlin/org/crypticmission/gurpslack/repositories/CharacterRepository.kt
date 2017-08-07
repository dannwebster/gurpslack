package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByAbbrev = HashMap<String, CharacterRoller>()

    fun add(characterName: String, abbrev: String) : Boolean {
        if (charactersByAbbrev.get(abbrev) != null) {
            return false
        } else {
            charactersByAbbrev[abbrev] = CharacterRoller(characterName, randomizer)
            return true
        }
    }

    fun get(abbrev: String) = charactersByAbbrev[abbrev]
    fun remove(abbrev: String) = charactersByAbbrev.remove(abbrev)
}
package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer

/**
 */

class CharacterRoller(val characterName: String,
                      val randomizer: Randomizer = Ra,
                      attributes: Map<String, Attribute> = emptyMap(),
                      damages: Map<String, Attribute> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes)
    val damages = HashMap<String, Attribute>(damages)

    companion object {
        val DEFAULT_ATTIRIBUTE_VALUE = 10
    }

    fun rollVsAttribute(name: String, modifier: Int = 0) =
        attributes.getOrPut(name) {Attribute(name, DEFAULT_ATTIRIBUTE_VALUE)}.roll(randomizer)
}
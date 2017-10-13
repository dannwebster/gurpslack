package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.message.toKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {
    private val logger = LoggerFactory.getLogger(CharacterRepository::class.java)

    val randomizer = Randomizer.system()

    private val charactersByKey = HashMap<String, CharacterRoller>()
    private val charactersByUserName = HashMap<String, Pair<String, CharacterRoller>>()

    fun add(abbrev: String, userName: String?, characterName: String) : Boolean {
        val key = abbrev.toKey()
        return when (charactersByKey.get(key.toKey())) {
            null -> {
                charactersByKey[key] = CharacterRoller(randomizer, characterName)
                if (userName != null && !userName.isNullOrBlank()) {
                    charactersByUserName[userName.toKey()] = Pair(key, CharacterRoller(randomizer, characterName))
                }
                return true
            }
            else -> false
        }
    }

    fun put(key: String, userName: String?, character: CharacterRoller) {
        val k = key.toKey()
        charactersByKey[k] = character
        if (userName != null) {
            charactersByUserName[userName.toKey()] = Pair(k, character)
        }
    }

    fun getByKey(abbrev: String) = charactersByKey[abbrev.toKey()]
    fun removeByKey(abbrev: String) = charactersByKey.remove(abbrev.toKey())

    fun getByUserName(userName: String) : Pair<String, CharacterRoller>? {
        logger.debug("getting character '${userName}' from ${charactersByUserName.keys}")
        return charactersByUserName[userName.toKey()] }
    fun removeByUserName(userName: String) = charactersByUserName.remove(userName.toKey())

    fun listByKey() = charactersByKey.toList()
    fun listByUserName() = charactersByUserName.toList()
}
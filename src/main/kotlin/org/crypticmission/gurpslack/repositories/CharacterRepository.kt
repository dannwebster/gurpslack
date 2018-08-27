package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.message.toKey
import org.crypticmission.gurpslack.message.trackedStatsAttachments
import org.crypticmission.gurpslack.model.TrackedValue
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository(val trackedStatService: TrackedStatService, val trackedAmountRespository: TrackedAmountRespository) {
    private val logger = LoggerFactory.getLogger(CharacterRepository::class.java)

    val randomizer = Randomizer.system()

    private val charactersByKey = HashMap<String, CharacterRoller>()
    private val charactersByUserName = HashMap<String, Pair<String, CharacterRoller>>()

    fun add(abbrev: String, userName: String?, characterName: String) : Boolean {
        val key = abbrev.toKey()
        return when (charactersByKey.get(key.toKey())) {
            null -> {
                charactersByKey[key] = CharacterRoller(randomizer, characterName, userName?: "GM")
                if (userName != null && !userName.isNullOrBlank()) {
                    charactersByUserName[userName.toKey()] = Pair(key, CharacterRoller(randomizer, characterName, userName))
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

    fun getByKey(abbrev: String): CharacterRoller? {
      val char = charactersByKey[abbrev.toKey()]
      if (char != null) {
          addTrackedStats(abbrev, char)
      }
      return char
    }

    private fun addTrackedStats(abbrev: String, char: CharacterRoller) {
        val trackedValues = trackedStatService.getTrackedStatMap(abbrev)
        char.trackedValues.entries.forEach { (statName, stat) ->
            val currentValue = trackedValues[statName]
            if (currentValue != null) {
                stat.currentValue = currentValue
            }
        }
        char.addTrackedAmount(trackedAmountRespository.findByCharacterKey(abbrev))
    }

    fun getByUserName(userName: String) : Pair<String, CharacterRoller>? {
        logger.debug("getting character '${userName}' from ${charactersByUserName.keys}")
        val abbrevToCharacter = charactersByUserName[userName.toKey()]
        if (abbrevToCharacter != null) {
            addTrackedStats(abbrevToCharacter.first, abbrevToCharacter.second)
        }
        return abbrevToCharacter
    }

    fun removeByKey(abbrev: String): CharacterRoller? {
        val username = charactersByUserName.entries.first { it.value.first == abbrev }.key
        val c = charactersByKey.remove(abbrev.toKey())
        if (c != null) {
            charactersByUserName.remove(username)
        }
        return c
    }

    fun removeByUserName(userName: String): CharacterRoller? {
        val pair = charactersByUserName.remove(userName.toKey())
        if (pair != null) {
            val key = pair.first
            charactersByKey.remove(key.toKey())
        }
        return pair?.second
    }

    fun listByKey() = charactersByKey.toList()
    fun listByUserName() = charactersByUserName.toList()
}
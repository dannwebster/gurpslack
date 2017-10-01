package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.model.toKey
import org.springframework.stereotype.Repository

/**
 */

@Repository
class CharacterRepository() {

    val randomizer = Randomizer.system()

    private val charactersByKey = HashMap<String, CharacterRoller>()
    private val charactersByUserName = HashMap<String, CharacterRoller>()

    fun add(abbrev: String, userName: String?, characterName: String) : Boolean {
        val key = abbrev.toKey()
        return when (charactersByKey.get(key.toKey())) {
            null -> {
                charactersByKey[key] = CharacterRoller(randomizer, characterName)
                if (userName != null && !userName.isNullOrBlank()) {
                    charactersByUserName[userName.toKey()] = CharacterRoller(randomizer, characterName)
                }
                return true
            }
            else -> false
        }
    }

    fun put(key: String, userName: String?, character: CharacterRoller) {
        charactersByKey[key.toKey()] = character
        println(userName)
        if (userName != null) {
            println("adding character ${userName}")
            charactersByUserName[userName.toKey()] = character
        }
    }

    fun getByKey(abbrev: String) = charactersByKey[abbrev.toKey()]
    fun removeByKey(abbrev: String) = charactersByKey.remove(abbrev.toKey())

    fun getByUserName(userName: String) : CharacterRoller? {
        println("getting ${userName}");
        return charactersByUserName[userName.toKey()] }
    fun removeByUserName(userName: String) = charactersByUserName.remove(userName.toKey())

    fun listByKey() = charactersByKey.toList()
    fun listByUserName() = charactersByUserName.toList()
}
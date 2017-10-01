package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.CharacterSheetRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 */
@Component
class Initializer(
        val characterSheetRepository: CharacterSheetRepository,
        val characterLoader: CharacterLoader,
        val randomizer: Randomizer,
        val characterRepository: CharacterRepository) {

    private val logger = LoggerFactory.getLogger(Initializer::class.java)

    init {
        loadAllCharacterSheets()
    }

    fun loadAllCharacterSheets() {
        val characterSheets = characterSheetRepository.findAll()
        characterSheets.forEach { characterSheet ->
            logger.info("loading character ${characterSheet.characterKey} from database")

            val characterData = characterLoader.load(characterSheet.characterXml) ?:
                    throw IllegalArgumentException("could not load character ${characterSheet.characterKey}")
            val characterRoller = characterData.toRoller(randomizer)
            characterRepository.put(characterSheet.characterKey, characterSheet.userName, characterRoller)
        }
    }
}
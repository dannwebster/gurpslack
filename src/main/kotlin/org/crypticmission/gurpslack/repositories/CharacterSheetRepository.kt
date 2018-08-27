package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.entities.CharacterSheet
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component

@Component
class CharacterSheetService(val characterSheetRepository: CharacterSheetRepository) {
    private val logger = LoggerFactory.getLogger(CharacterSheetService::class.java)

    fun saveOrUpdate(characterSheet: CharacterSheet) : CharacterSheet {
        logger.info("Saving character ${characterSheet.characterKey}")
        val old = characterSheetRepository.findOneByCharacterKey(characterSheet.characterKey)
        val new = if (old != null) {
            logger.info("Saving over found character ${characterSheet.characterKey}")
            old.characterXml = characterSheet.characterXml
            characterSheetRepository.save(old)
        } else {
            logger.info("Creating new character ${characterSheet.characterKey}")
            characterSheetRepository.save(characterSheet)
        }
        return new
    }
}

interface CharacterSheetRepository : CrudRepository<CharacterSheet, Long> {
    fun findOneByCharacterKey(characterKey: String) : CharacterSheet?
    fun findByCampaign(campaign: String) : CharacterSheet?
}
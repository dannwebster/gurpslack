package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterSheet
import org.springframework.data.repository.CrudRepository

interface CharacterSheetRepository : CrudRepository<CharacterSheet, Long> {
}
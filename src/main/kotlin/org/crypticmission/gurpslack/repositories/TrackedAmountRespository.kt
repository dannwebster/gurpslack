package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.entities.TrackedAmount
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 */
@Repository
interface TrackedAmountRespository : CrudRepository<TrackedAmount, Long> {
    fun findOneByCharacterKeyAndAbbreviation(characterKey: String, abbreviation: String): TrackedAmount?
    fun findByCharacterKeyAndType(characterKey: String, abbreviation: String): List<TrackedAmount>
    fun findByCharacterKey(characterKey: String): List<TrackedAmount>
}
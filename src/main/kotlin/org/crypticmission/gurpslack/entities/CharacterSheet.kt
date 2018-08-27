package org.crypticmission.gurpslack.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
data class CharacterSheet(
    var characterKey: String = "",
    var characterXml: String = "",
    var userName: String = "",
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)

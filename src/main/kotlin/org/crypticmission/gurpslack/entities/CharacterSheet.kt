package org.crypticmission.gurpslack.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id


@Entity
class CharacterSheet(
    var characterKey: String = "",
    var characterXml: String = "",
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)

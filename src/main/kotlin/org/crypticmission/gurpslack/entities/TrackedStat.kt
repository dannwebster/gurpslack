package org.crypticmission.gurpslack.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 */
@Entity
data class TrackedStat(
    var characterKey: String = "",
    var statName: String = "",
    var value: Int = 10,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0)

package org.crypticmission.gurpslack.entities

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class TrackedAmount(
    var characterKey: String = "",
    var abbreviation: String = "",
    var name: String = "",
    var notes: String = "",
    var type: String = "",
    var sortOrder: Int = 0,
    var maxValue: Int = 10,
    var currentValue: Int = 10,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0): Comparable<TrackedAmount> {

    override fun compareTo(other: TrackedAmount): Int = this.sortOrder - other.sortOrder
}

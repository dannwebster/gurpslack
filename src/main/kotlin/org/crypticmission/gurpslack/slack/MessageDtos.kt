package org.crypticmission.gurpslack.slack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.annotation.JsonCreator



enum class ActionType {
    button,
    select;

    companion object {
        @JsonCreator
        fun forValue(value: String): ActionType? {
            return ActionType.values().find { value == it.name }
        }
    }
}


class Option() {
    lateinit var value: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class ActionTaken() {
    lateinit var name: String
    lateinit var type: ActionType
    var value: String? = null
    var selectedOptions: List<Option>? = null

    fun selectedValue(): String? = this.selectedOptions?.first()?.value
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class MessageData() {
    lateinit var user: User
    lateinit var token: String
    lateinit var actions: Array<ActionTaken>
    lateinit var callbackId: String
    lateinit var responseUrl: String
}



@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class User() {
    lateinit var id: String
    lateinit var name: String
}
enum class MenuType() { DAMAGE, MOD, TRACKED }
enum class CharacterSections(val menuType: MenuType) {
    PRIMARY_ATTRIBUTES(MenuType.MOD),
    DERIVED_ATTRIBUTES(MenuType.MOD),
    TRACKED_STATS(MenuType.TRACKED),
    SKILLS(MenuType.MOD),
    MELEE_ATTACKS(MenuType.DAMAGE),
    RANGED_ATTACKS(MenuType.DAMAGE)
}
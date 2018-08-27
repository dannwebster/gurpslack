package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.parseDamage
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 */
class CharacterLoaderTest {

    private fun loadCharacter(fileName: String): CharacterData {
        val reader = this::class.java.getResourceAsStream(fileName).bufferedReader()
        val subject = CharacterLoader()

        // when
        val character = subject.load(reader) ?: throw IllegalArgumentException()
        return character
    }

    val nora = loadCharacter("/NoraBlackburn.gcs")
    val ev = loadCharacter("/Everett O'Connell.gcs")
    val rc = loadCharacter("/rc-cleveland.gcs")

    @Test fun shouldParseNoraWhenLoadedFromFile() {
        // given
        val character = nora

        // then
        assertNotNull(character)
        assertEquals("Nora Blackburn", character.name)
        assertEquals("Tinsley Webster", character.playerName)
    }

    @Test fun shouldLoadEquipmentFromContainer() {
        // given
        val character = ev

        // then
        assertNotNull(character)
        assertEquals("Everett O'Connell", character.name)
        assertEquals("Stewart", character.playerName)

        val pistol = character.rangedAttacks.get("FN-Browning High Power, 9x19mm") ?:
                throw AssertionError("no pistol Damage")

        assertEquals("FN-Browning High Power, 9x19mm", pistol.attackName)
        assertEquals(parseDamage("2d+2 pi"), pistol.damageSpec)

        val slugs = "Slugs: Shotgun (FN-Browning Auto-5, 12G, 2.75\")"
        val shotgunSlugs = character.rangedAttacks.get(slugs) ?: throw AssertionError("no shotgun slug Damage")
        assertEquals(slugs, shotgunSlugs.attackName)
        assertEquals(parseDamage("4d+4 pi++"), shotgunSlugs.damageSpec)

        val shot = "Buckshot: Shotgun (FN-Browning Auto-5, 12G, 2.75\")"
        val shotgunShot = character.rangedAttacks.get(shot) ?: throw AssertionError("no shotgun shot Damage")
        assertEquals(shot, shotgunShot.attackName)
        assertEquals(parseDamage("1d+1 pi"), shotgunShot.damageSpec)

        assertEquals(7, character.rangedAttacks.size)
        assertEquals(2, character.meleeAttacks.size)

    }

    @Test
    fun shouldDefaultCurrentFpAndHpToMaxFileWhenFileDoesNotHaveThemSet() {
        // given
        val character = ev

        assertEquals(13, character.ht)
        assertEquals(3, character.fp)
        assertEquals(16, character.maxFp)
        assertEquals(10, character.currentFp)

        assertEquals(12, character.st)
        assertEquals(1, character.hp)
        assertEquals(13, character.maxHp)
        assertEquals(9, character.currentHp)
    }
    @Test
    fun shouldLoadCurrentHpFpFromaFileWhenItHasTheSet() {
        // given
        var character = nora

        // then
        assertEquals(12, character.ht)
        assertEquals(0, character.fp)
        assertEquals(12, character.maxFp)
        assertEquals(12, character.currentFp)

        assertEquals(10, character.st)
        assertEquals(1, character.hp)
        assertEquals(11, character.maxHp)
        assertEquals(11, character.currentHp)
    }
    @Test fun shouldParseRcWhenLoadedFromFile() {
        // given
        val character = rc

        // then
        assertNotNull(character)
        assertEquals("R.C. Cleveland", character.name)
        assertEquals("marcfaletti", character.playerName)

        assertEquals(10, character.st)
        assertEquals(13, character.dx)
        assertEquals(14, character.iq)
        assertEquals(11, character.ht)
        assertEquals(15, character.will)
        assertEquals(12, character.per)

        val skills = character.skills
        assertEquals(46, skills.size)
        val skill = character.skills.get("Acting") ?: throw AssertionError("no Acting Skill")
        assertEquals("Acting", skill.name)
        assertEquals(13, skill.level)

        // testing wildcard skill levels
        val bureaucrat = character.skills.get("Bureaucrat!")
        assertEquals(11, bureaucrat?.level)

        val cleric = character.skills.get("Cleric!")
        assertEquals(12, cleric?.level)

        val conspiracy = character.skills.get("Conspiracy!")
        assertEquals(13, conspiracy?.level)

        val encyclopedist = character.skills.get("Encyclopedist!")
        assertEquals(14, encyclopedist?.level)

        val spiritLore = character.skills.get("Hidden Lore (Spirit Lore)")
        assertEquals(13, spiritLore?.level)

        val guns = character.skills.get("Guns/TL5^ (Pistol)")
        assertEquals(14, guns?.level)

        val swungKnife = character.meleeAttacks.get("Large Knife (Swung)") ?: throw AssertionError("no Large Knife (Swung)")
        assertEquals("Large Knife (Swung)", swungKnife.attackName)
        assertEquals(parseDamage("1d6-2 cut"), swungKnife.damageSpec)

        val thrustKnife = character.meleeAttacks.get("Large Knife (Thrust)") ?: throw AssertionError("no Large Knife (Thrust)")
        assertEquals("Large Knife (Thrust)", thrustKnife.attackName)
        assertEquals(parseDamage("1d6-2 imp"), thrustKnife.damageSpec)

        val rifle = character.rangedAttacks.get("Lever-Action Carbine, .30") ?: throw AssertionError("no rifleDamage")
        assertEquals("Lever-Action Carbine, .30", rifle.attackName)
        assertEquals(parseDamage("5d pi"), rifle.damageSpec)
//j
//        <damage>sw-2 cut</damage>
//        <strength>6</strength>
//        <usage>Swung</usage>
//        <reach>C,1</reach>
//        <parry>-1</parry>
//        <block>No</block>
    }
}
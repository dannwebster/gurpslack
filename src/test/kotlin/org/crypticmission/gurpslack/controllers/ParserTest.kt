package org.crypticmission.gurpslack.controllers

import org.junit.Test
import org.junit.Assert.*

/**
 */
class ParserTest {

     @Test fun shouldReturnNullWhenEmptyVsData() {
        // given
        val line = ""

        // when
        val output = parseVsData(line)

        // then
        assertNull(output)
    }

    @Test fun shouldReturnNullWhenOneTokenInData() {
        // given
        val line = "nora"

        // when
        val output = parseVsData(line)

        // then
        assertNull(output)
    }

    @Test fun shouldReturnFullDataWhenTwoTokens() {
        // given
        val line = "nora stealth"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("stealth", skill)
        assertEquals(0, modifier)
    }

    @Test fun shouldReturnFullDataWhenThreeTokens() {
        // given
        val line = "nora area knowledge"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("area knowledge", skill)
        assertEquals(0, modifier)
    }

    @Test fun shouldReturnFullDataWhenTwoTokensModiferNoSpace() {
        // given
        val line = "nora stealth-3"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("stealth", skill)
        assertEquals(-3, modifier)
    }


    @Test fun shouldReturnFullDataWhenTwoTokensModiferWithSpace() {
        // given
        val line = "nora stealth +3"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("stealth", skill)
        assertEquals(3, modifier)
    }

    @Test fun shouldReturnFullDataWhenThreeTokensModifierNoSpace() {
        // given
        val line = "nora area knowledge+3"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("area knowledge", skill)
        assertEquals(3, modifier)
    }

    @Test fun shouldReturnFullDataWhenThreeTokensModifierWithSpace() {
        // given
        val line = "nora area knowledge -3"

        // when
        val (name, skill, modifier) = parseVsData(line) ?: throw IllegalArgumentException()

        // then
        assertEquals("nora", name)
        assertEquals("area knowledge", skill)
        assertEquals(-3, modifier)
    }
}
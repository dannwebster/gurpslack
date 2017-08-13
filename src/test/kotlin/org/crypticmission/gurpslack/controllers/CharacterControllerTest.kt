package org.crypticmission.gurpslack.controllers

import org.junit.Test

import org.junit.Assert.*

/**
 */
class CharacterControllerTest {
    @Test fun shouldGivePairWhenTextIsOneWord() {
        // given
        val text = "ab"

        // when
        val pair = parseName(text)

        // then
        assertEquals(Pair("ab", "ab"), pair)
    }

    @Test fun shouldCreateNameAndAbbveWhenTextIsTwoWords() {
        // given
        val text = "ab\tcd"

        // when
        val pair = parseName(text)

        // then
        assertEquals(Pair("ab", "cd"), pair)

    }

    @Test fun shouldCreateLongNameWithMultipleWords() {
        // given
        val text = "ww Warren  E.  \t Worthington, III"

        // when
        val pair = parseName(text)

        // then
        assertEquals(Pair("ww", "Warren E. Worthington, III"), pair)

    }

}
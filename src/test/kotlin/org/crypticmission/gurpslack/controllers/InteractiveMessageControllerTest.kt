package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.model.Attribute
import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.model.ShotsFiredCalculator
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 */
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class InteractiveMessageControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    val body = """
{
"actions": [
{
"name": "skill",
"value": "rc@gambling@-1",
"type": "button"
}
],
"callback_id": "comic_1234_xyz",
"team": {
"id": "T47563693",
"domain": "watermelonsugar"
},
"channel": {
"id": "C065W1189",
"name": "forgotten-works"
},
"user": {
"id": "U045VRZFT",
"name": "brautigan"
},
"action_ts": "1458170917.164398",
"message_ts": "1458170866.000004",
"attachment_id": "1",
"token": "xAB3yVzGS4BQ3O9FACTa8Ho4",
"original_message": {"text":"New comic book alert!","attachments":[{"title":"The Further Adventures of Slackbot","fields":[{"title":"Volume","value":"1","short":true},{"title":"Issue","value":"3","short":true}],"author_name":"Stanford S. Strickland","author_icon":"https://api.slack.comhttps://a.slack-edge.com/bfaba/img/api/homepage_custom_integrations-2x.png","image_url":"http://i.imgur.com/OJkaVOI.jpg?1"},{"title":"Synopsis","text":"After @episod pushed exciting changes to a devious new branch back in Issue 1, Slackbot notifies @don about an unexpected deploy..."},{"fallback":"Would you recommend it to customers?","title":"Would you recommend it to customers?","callback_id":"comic_1234_xyz","color":"#3AA3E3","attachment_type":"default","actions":[{"name":"recommend","text":"Recommend","type":"button","value":"recommend"},{"name":"no","text":"No","type":"button","value":"bad"}]}]},
"response_url": "https://hooks.slack.com/actions/T47563693/6204672533/x7ZLaiVMoECAW50Gw1ZYAXEM"
}
""".trim()

    val shotsFiredCalculator = ShotsFiredCalculator();
    @Test
    fun shouldParseBodyWhenPosted() {
        // given

        // when
        mockMvc
                .perform(post("/buttons")
                        .param("payload", body)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andDo { print(it) }
                .andExpect { status().isOk }
                .andExpect { content().contentType("text/html") }
                .andExpect { content().encoding("UTF-8") }
    }

    @Test
    fun shouldRollSkillWhenCallingCharacterWithSkill() {
        // given
        val cr = CharacterRoller(Randomizer.MAX, "character name")
        cr.addSkill(Attribute("skill-name", 10))
        val repo = CharacterRepository()
        repo.put("character-key", null, cr)

        val subject = InteractiveMessageController(repo, shotsFiredCalculator)

        // when
        val message = subject.skill("character-key", "skill-name", 0)

        // then
        assertEquals("""
                *Roll:* character name Rolled vs skill-name (10)
                > *- Outcome:* Critical Failure
                > *- Roll:* :d6-6: :d6-6: :d6-6: = 18
                > *- Effective Level:* 10
                > *- Margin of Failure:* 8
                > *- Attribute:* skill-name: 10
                > *- Modifier:* +0""".trimIndent(),
                message.text)

    }

    @Test
    fun shouldMessageMissingCharacterWhenCallingWrongCharacter() {
        // given
        val repo = CharacterRepository()

        val subject = InteractiveMessageController(repo, shotsFiredCalculator)

        // when
        val message = subject.skill("character-key", "skill-name", 0)

        // then
        assertEquals("unable to find character 'character-key'", message.text)

    }

    @Test
    fun shouldMessageMissingSkillWhenCallingWrongSkill() {
        // given
        val cr = CharacterRoller(Randomizer.MAX, "character name")
        val repo = CharacterRepository()
        repo.put("character-key", null, cr)

        val subject = InteractiveMessageController(repo, shotsFiredCalculator       )

        // when
        val message = subject.skill("character-key", "skill-name", 0)

        // then
        assertEquals("unable to find skill 'skill-name' for character key 'character-key'", message.text)

    }
}
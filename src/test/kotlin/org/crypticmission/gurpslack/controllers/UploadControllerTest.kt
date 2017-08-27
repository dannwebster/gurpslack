package org.crypticmission.gurpslack.controllers

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.MimeType

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UploadControllerTest {


    @Autowired
    lateinit var mockMvc: MockMvc

    @Test fun shouldReturnPageWhenGetCharacter() {
        // given

        // when
        val result = mockMvc.perform(get("/character"))

        // then
        result.andExpect { status().isOk }
                .andExpect { content().contentType("text/html") }
                .andExpect { content().encoding("UTF-8") }
    }

    @Test fun shouldReturnRedirectWhenUploadCharacter() {
        // given
        val file = this.javaClass.getResourceAsStream("/rc-cleveland.gcs")
        assertNotNull(file)
        val request = multipart("/character")
                .file(MockMultipartFile("file", file))
                .param("key", "character-key")

        // when
        val result = mockMvc.perform(request)

        // then
        println(result)
        result.andExpect { status().isOk }
                .andExpect { content().contentType("text/html") }
                .andExpect { content().encoding("UTF-8") }
    }
}
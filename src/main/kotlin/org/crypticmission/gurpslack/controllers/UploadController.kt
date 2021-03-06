package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.loader.CharacterLoader
import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.entities.CharacterSheet
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.CharacterSheetService
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest



/**
 */
@Controller
class UploadController(val characterRepository : CharacterRepository,
                       val characterLoader: CharacterLoader,
                       val characterSheetService: CharacterSheetService,
                       val randomizer: Randomizer) {

    var logger = LoggerFactory.getLogger(UploadController::class.java)


    @GetMapping("/character", "/")
    fun getCharacterPage(@RequestParam key: String?, @RequestParam campaign: String?) : ModelAndView {
        val message = if (key != null) {
            var character = characterRepository.getByKey(key)
            if (character != null)
                "Added character '${character.characterName}' under key '${key}'"
            else
                "Upload a gcs character file"
        } else  {
            "Upload a gcs character file"
        }
        val byKey = characterRepository.listByKey()
        val byUsername = characterRepository.listByUserName()
        return ModelAndView("uploadForm", mapOf(
                "message" to message,
                "characterByKeyList" to byKey,
                "characterByUsernameList" to byUsername
        ))
    }

    @PostMapping("/character")
    fun postCharacter(@RequestParam key: String,
                      @RequestParam username: String,
                      @RequestParam("file") file: MultipartFile) : String {
        addCharacter(key, username, file)
        return "redirect:/character?key=${key}"
    }

    // Total control - setup a model and return the view name yourself. Or
    // consider subclassing ExceptionHandlerExceptionResolver (see below).
    @ExceptionHandler(Exception::class)
    fun handleError(req: HttpServletRequest, ex: Exception): ModelAndView {
        logger.error("Failed!", ex)
        val mav = ModelAndView()
        mav.addObject("exception", ex)
        mav.addObject("url", req.getRequestURL())
        mav.viewName = "error"
        return mav
    }

    fun addCharacter(key: String, userName: String, file: MultipartFile): CharacterRoller =
            file.inputStream.bufferedReader().use { reader ->

                val xml = reader.readText()
                val characterSheet = CharacterSheet(key, xml, userName)
                characterSheetService.saveOrUpdate(characterSheet)

                val characterData = characterLoader.load(xml)
                characterData ?: throw IllegalArgumentException("Unable to parse file to create character")
                val character = characterData.toRoller(randomizer)
                characterRepository.put(key, userName, character)
                logger.info("Successfully added ${key} to total ${characterRepository.listByKey().size} characters")
                character
            }
}
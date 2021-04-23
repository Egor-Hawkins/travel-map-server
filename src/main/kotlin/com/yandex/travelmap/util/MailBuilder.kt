package com.yandex.travelmap.util


import org.springframework.stereotype.Service
import java.io.File

@Service
class MailBuilder {
    fun buildEmail(link: String): String {
        return File("src/main/resources/email/ConfirmationMessage.html").readLines().joinToString("").replace("\$link", link)
    }
}
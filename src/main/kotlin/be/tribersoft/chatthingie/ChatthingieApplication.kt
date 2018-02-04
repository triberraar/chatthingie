package be.tribersoft.chatthingie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChatthingieApplication

fun main(args: Array<String>) {
    runApplication<ChatthingieApplication>(*args)
}

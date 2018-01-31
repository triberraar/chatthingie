package be.tribersoft.chatthingie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableConfigurationProperties(JwtConfiguration::class)
class ChatthingieApplication

fun main(args: Array<String>) {
    runApplication<ChatthingieApplication>(*args)
}

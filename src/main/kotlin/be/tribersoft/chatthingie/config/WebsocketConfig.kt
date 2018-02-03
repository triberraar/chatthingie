package be.tribersoft.chatthingie.config

import be.tribersoft.chatthingie.ws.ChatHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebsocketConfig(private val chatHandler: ChatHandler, private val environment: Environment): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
      val registration = registry.addHandler(chatHandler, "/chat")
      if(environment.activeProfiles.contains("dev")) registration.setAllowedOrigins("*")
    }

}

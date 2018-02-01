package be.tribersoft.chatthingie.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChatHandler : TextWebSocketHandler() {

    override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        println(message)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
      // http session id: session.handshakeHeaders["Cookie"][0].split('=')[1]

        println("joiner")
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        println("leaver ${status}")
    }
}

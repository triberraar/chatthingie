package be.tribersoft.chatthingie.ws

import be.tribersoft.chatthingie.domain.User
import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.rest.RoomJson
import be.tribersoft.chatthingie.state.UserSessionsState
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KLogging
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.session.SessionDestroyedEvent
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.time.LocalDateTime
import java.util.*

@Component
class ChatHandler(private val userSessionsState: UserSessionsState, private val objectMapper: ObjectMapper, private val chatMessageHandler: ChatMessageHandler) : TextWebSocketHandler() {
  companion object : KLogging()

  override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    val messagePayload = message.payload
    try {
      val mappedMessage = objectMapper.readValue(messagePayload, Message::class.java)
      if(mappedMessage.type == MESSAGE_TYPES.CHAT) {
        chatMessageHandler.handle(objectMapper.readValue(messagePayload, ChatMessage::class.java).message, session)
      }
    } catch (e: Throwable) {
      logger.error { "Can't understand ${messagePayload}" }
    }
  }

  override fun afterConnectionEstablished(session: WebSocketSession) {
    session.handshakeHeaders["Cookie"]!!.forEach { cookies ->
      cookies.split(';').forEach { cookie ->
        if (cookie.startsWith("JSESSIONID", true)) {
          val httpSessionId = cookie.split('=')[1].trim()
          val user = (session.principal as UsernamePasswordAuthenticationToken).principal as User
          val websocketSessionId = session.id
          userSessionsState.userWebSocketConnected(user.id, httpSessionId, websocketSessionId, session)
        }
      }
    }
  }

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
    val websocketSessionId = session.id
    userSessionsState.userWebSocketClosed(websocketSessionId)
  }
}

@Component
class SessionEndedListener(private val userSessionsState: UserSessionsState) : ApplicationListener<SessionDestroyedEvent> {
  override fun onApplicationEvent(event: SessionDestroyedEvent) {
    // the session has ended so we should clean up websockets if we have any.
    userSessionsState.userSessionClosed(event.id)
  }
}

@Component
class ChatMessageHandler(private val userSessionsState: UserSessionsState, private val userRepository: UserRepository) {
  companion object : KLogging()
  fun handle(message: ChatContent, session: WebSocketSession) {
    val userSession = userSessionsState.getUserByWebsocketSession(session.id)
    if(userSession == null) {
      logger().warn { "Websocket session  ${session.id} not connected, dropping message ${message}" }
      return
    }
    if(userSession.userId != message.userId) {
      logger().error { "Somebody is trying to be an imposter ${userSession.userId} vs ${message.userId}"}
    }
    if(!userSessionsState.usersInRoom(message.roomId).contains(userSession.userId)) {
      logger().error { "User ${userSession.userId} is not in the room ${message.roomId}" }
    }
    val user = userRepository.getById(userSession.userId)
    val right = user.rights.find { it.roomId == message.roomId }
    if(right == null || !right.write) {
      logger.error { "User ${userSession.userId} is not allowed to write in room ${message.roomId}" }
    }
    message.username = user.username
    userSessionsState.forwardChat(message)
    // TODO save the message if the room requires
  }

}

object MESSAGE_TYPES {
  val ROOM = "room"
  val CHAT = "chat"
}

open class Message(val type: String)
data class RoomMessage(val room: RoomJson) : Message(MESSAGE_TYPES.ROOM)
data class ChatMessage(val message: ChatContent) : Message(MESSAGE_TYPES.CHAT)
data class ChatContent(val message: String, val userId: UUID, val roomId: UUID, var username: String?, val timestamp: LocalDateTime = LocalDateTime.now())

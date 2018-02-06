package be.tribersoft.chatthingie.state

import be.tribersoft.chatthingie.domain.RoomRepository
import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.rest.toRoomJson
import be.tribersoft.chatthingie.rest.toRoomUserJson
import be.tribersoft.chatthingie.ws.ChatContent
import be.tribersoft.chatthingie.ws.ChatMessage
import be.tribersoft.chatthingie.ws.RoomMessage
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import mu.KLogging
import org.springframework.security.core.session.SessionRegistry
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*

/*
  This one holds the in memory state and mapping of all http sessions to websocket sessions to users to rooms.
  Probably can use some love to extract stuff in services
 */
@Component
class UserSessionsState(private val userSessions: MutableList<UserSession> = mutableListOf(),
                        private val objectMapper: ObjectMapper,
                        private val userRepository: UserRepository,
                        private val roomRepository: RoomRepository,
                        private val sessionRegistry: SessionRegistry) {
  companion object: KLogging()

  @Synchronized fun userWebSocketConnected(userId: UUID, httpSessionId: String, webSocketSessionId: String, webSocketSession: WebSocketSession) {
    userSessions.add(UserSession(userId, httpSessionId, webSocketSessionId, webSocketSession))
  }

  @Synchronized fun userWebSocketClosed(webSocketSessionId: String) {
    val userSessionForSession = userSessions.filter { it.webSocketSessionId == webSocketSessionId && it.roomId != null}
    userSessionForSession.forEach { leaveRoom(it.userId, it.httpSessionId, it.roomId!!) }
    userSessions.removeIf { it.webSocketSessionId == webSocketSessionId }
  }

  @Synchronized fun userSessionClosed(httpSessionId: String) {
    val userSessionForSession = userSessions.filter { it.httpSessionId == httpSessionId }
    userSessionForSession.forEach {
      if(it.roomId != null) leaveRoom(it.userId, it.httpSessionId, it.roomId!!)
      it.webSocketSession.close()
    }
  }

  @Synchronized fun usersInRoom(roomId: UUID) = userSessions.filter { it.roomId == roomId }.map { it.userId }

  @Synchronized fun leaveRoom(userId: UUID, httpSessionId: String, previousRoom: UUID) {
    userSessions.forEach {
      if(userRepository.getById(it.userId).rights.find { it.roomId == previousRoom } != null) {
        val userIds = userSessions.filter { it.roomId == previousRoom && httpSessionId != it.httpSessionId }.map { it.userId }
        val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
        val right = userRepository.getById(it.userId).rights.find { it.roomId == previousRoom }!!
        if(it.webSocketSession.isOpen) {
          try {
            it.webSocketSession.sendMessage(TextMessage(objectMapper.writeValueAsString(RoomMessage(right.toRoomJson(roomUserJsons, roomRepository.getById(right.roomId).name)))))
          } catch (e: Throwable) {
            logger.warn { "Sending message to WS failed $e" }
          }
        }
      }
    }
    val userSessionsForHttpSession = userSessions.filter { it.httpSessionId == httpSessionId }
    userSessionsForHttpSession.forEach { it.roomId = null }
  }

  @Synchronized fun joinRoom(userId: UUID, httpSessionId: String, currentRoom: UUID) {
    val userSessionsForHttpSession = userSessions.filter { it.httpSessionId == httpSessionId }
    userSessionsForHttpSession.forEach { it.roomId = currentRoom }

    userSessions.forEach {
      if(userRepository.getById(it.userId).rights.find { it.roomId == currentRoom } != null) {
        val userIds = usersInRoom(currentRoom)
        val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
        val right = userRepository.getById(it.userId).rights.find { it.roomId == currentRoom }!!
        if(it.webSocketSession.isOpen) {
          try {
            it.webSocketSession.sendMessage(TextMessage(objectMapper.writeValueAsString(RoomMessage(right.toRoomJson(roomUserJsons, roomRepository.getById(right.roomId).name)))))
          } catch (e: Throwable) {
            logger.warn { "Sending message to WS failed $e" }
          }
        }
      }
    }
  }

  @Synchronized fun getUserByWebsocketSession(webSocketSessionId: String) = userSessions.find { it.webSocketSessionId == webSocketSessionId }

  fun forwardChat(message: ChatContent) {
    userSessions.filter { it.roomId == message.roomId }.forEach {
      if(it.webSocketSession.isOpen) {
        try {
          it.webSocketSession.sendMessage(TextMessage(objectMapper.writeValueAsString(ChatMessage(message))))
        } catch (e: Throwable) {
          logger.warn { "Sending message to WS failed $e" }
        }
      }
    }
  }

  fun numberOfConnections() = userSessions.size.toDouble()

  fun deactivate(userId: UUID) {
    userSessions.filter { it.userId == userId}.forEach {
      try {
        it.webSocketSession.close()
        sessionRegistry.removeSessionInformation(it.httpSessionId)
      } catch (e: Throwable) {
        logger.warn { "Closing websocket failed $e" }
      }
    }
    userSessions.removeIf { it.userId == userId }
  }
}

data class UserSession(val userId: UUID, val httpSessionId: String, val webSocketSessionId: String, val webSocketSession: WebSocketSession, var roomId: UUID? = null)

@Component
class ConnectionsMetric(private val metricRegistry: MeterRegistry, private val userSessionsState: UserSessionsState) {

      val connectionGauge = Gauge.builder("custom.connections", userSessionsState) { userSessionsState.numberOfConnections() }
        .description("a look at the connections")
        .register(metricRegistry)

}

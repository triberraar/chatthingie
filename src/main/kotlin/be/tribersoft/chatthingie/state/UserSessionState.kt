package be.tribersoft.chatthingie.state

import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.rest.toRoomJson
import be.tribersoft.chatthingie.rest.toRoomUserJson
import be.tribersoft.chatthingie.ws.RoomMessage
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.*
import kotlin.collections.ArrayList

@Component
class UserSessionsState(private val userSessions: MutableList<UserSession> = mutableListOf(),  private val objectMapper: ObjectMapper, private val userRepository: UserRepository) {

  @Synchronized fun userWebSocketConnected(userId: UUID, httpSessionId: String, webSocketSessionId: String, webSocketSession: WebSocketSession) {
    userSessions.add(UserSession(userId, httpSessionId, webSocketSessionId, webSocketSession))
  }

  @Synchronized fun userWebSocketClosed(webSocketSessionId: String) {
    userSessions.removeIf { it.webSocketSessionId == webSocketSessionId }
  }

  @Synchronized fun userSessionClosed(httpSessionId: String) {
    val userSession = userSessions.find { it.httpSessionId == httpSessionId }
    if(userSession != null) {
      userSession.webSocketSession.close()
    }
  }

  @Synchronized fun usersInRoom(roomId: UUID) = userSessions.filter { it.roomId == roomId }.map { it.userId }

  @Synchronized fun leaveRoom(userId: UUID, httpSessionId: String, previousRoom: UUID) {
    userSessions.forEach {
      if(userRepository.getById(it.userId).rights.find { it.room.id == previousRoom } != null) {
        val userIds = userSessions.filter { it.roomId == previousRoom && httpSessionId != it.httpSessionId }.map { it.userId }
        val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
        val right = userRepository.getById(it.userId).rights.find { it.room.id == previousRoom }!!
        it.webSocketSession.sendMessage(TextMessage(objectMapper.writeValueAsString(RoomMessage(right.toRoomJson(roomUserJsons)))))
      }
    }
    val userSession = userSessions.find { it.httpSessionId == httpSessionId }
    if(userSession != null) {
      userSession.roomId = null
    }
  }

  @Synchronized fun joinRoom(userId: UUID, httpSessionId: String, currentRoom: UUID) {
    val userSession = userSessions.find { it.httpSessionId == httpSessionId }
    if(userSession != null) {
      userSession.roomId = currentRoom
    }

    userSessions.forEach {
      if(userRepository.getById(it.userId).rights.find { it.room.id == currentRoom } != null) {
        val userIds = usersInRoom(currentRoom)
        val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
        val right = userRepository.getById(it.userId).rights.find { it.room.id == currentRoom }!!
        it.webSocketSession.sendMessage(TextMessage(objectMapper.writeValueAsString(RoomMessage(right.toRoomJson(roomUserJsons)))))
      }
    }
  }
}

data class UserSession(val userId: UUID, val httpSessionId: String, val webSocketSessionId: String, val webSocketSession: WebSocketSession, var roomId: UUID? = null)

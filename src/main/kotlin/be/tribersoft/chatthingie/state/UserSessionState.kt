package be.tribersoft.chatthingie.state

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Component
class UserSessionsState(private val userSessions: CopyOnWriteArrayList<UserSession> = CopyOnWriteArrayList()) {

  fun userWebSocketConnected(userId: UUID, httpSessionId: String, webSocketSessionId: String, webSocketSession: WebSocketSession) {
    userSessions.add(UserSession(userId, httpSessionId, webSocketSessionId, webSocketSession))
  }

  fun userWebSocketClosed(webSocketSessionId: String) {
    userSessions.removeIf { it.webSocketSessionId == webSocketSessionId }
  }

  fun userSessionClosed(httpSessionId: String) {
    val userSession = userSessions.find { it.httpSessionId == httpSessionId }
    if(userSession != null) {
      userSession.webSocketSession.close()
    }
  }

  fun usersInRoom(roomId: UUID) =userSessions.filter { it.roomId === roomId }.map { it.userId }
}

data class UserSession(val userId: UUID, val httpSessionId: String, val webSocketSessionId: String, val webSocketSession: WebSocketSession, var roomId: UUID? = null)

package be.tribersoft.chatthingie.ws

import be.tribersoft.chatthingie.domain.User
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.session.SessionDestroyedEvent
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Component
class ChatHandler(private val userSessionsState: UserSessionsState): TextWebSocketHandler() {

    override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        println(message)
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
      // http session id: session.handshakeHeaders["Cookie"][0].split('=')[1]
      val httpSessionId = session.handshakeHeaders["Cookie"]!![0].split('=')[1]
      val user = (session.principal as UsernamePasswordAuthenticationToken).principal as User
      val websocketSessionId = session.id
      userSessionsState.userWebSocketConnected(user.id, httpSessionId, websocketSessionId, session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus?) {
      val httpSessionId = session.handshakeHeaders["Cookie"]!![0].split('=')[1]
      val user = (session.principal as UsernamePasswordAuthenticationToken).principal as User
      val websocketSessionId = session.id
      userSessionsState.userWebSocketClosed(websocketSessionId)
    }
}

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
}

@Component
class SessionEndedListener(private val userSessionsState: UserSessionsState): ApplicationListener<SessionDestroyedEvent> {
  override fun onApplicationEvent(event: SessionDestroyedEvent) {
    // the session has ended so we should clean up websockets if we have any.
    userSessionsState.userSessionClosed(event.id)
  }
}

data class UserSession(val userId: UUID, val httpSessionId: String, val webSocketSessionId: String, val webSocketSession: WebSocketSession, var room: String? = null)

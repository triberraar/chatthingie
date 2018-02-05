package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.domain.Right
import be.tribersoft.chatthingie.domain.RoomRepository
import be.tribersoft.chatthingie.domain.User
import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.state.UserSessionsState
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping(path = arrayOf("/rooms"))
class RoomResource(private val userRepository: UserRepository, private val userSessionsState: UserSessionsState, private val roomRepository: RoomRepository) {
  companion object: KLogging()

  @GetMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun mine(principal: Principal): List<RoomJson> {
    val user = (principal as UsernamePasswordAuthenticationToken).principal as User
    val rights = userRepository.getById(user.id).rights
    return rights.map {
      val userIds = userSessionsState.usersInRoom(it.roomId)
      val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
      it.toRoomJson(roomUserJsons, roomRepository.getById(it.roomId).name)
    }
  }

  @PutMapping(path= arrayOf("/join"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun joinRoom(@RequestBody joinRoomJson: JoinRoomJson, principal: Principal, httpSession: HttpSession): RoomJson {
    val user = (principal as UsernamePasswordAuthenticationToken).principal as User
    if (user.rights.find { it.roomId == joinRoomJson.currentRoom } == null) {
      logger.error { "User ${user.username} trying to join unpermitted room ${joinRoomJson.currentRoom}" }
      throw RuntimeException("Not allowed to join this room")
    }

    if(joinRoomJson.previousRoom != null) userSessionsState.leaveRoom(user.id, httpSession.id, joinRoomJson.previousRoom)
    userSessionsState.joinRoom(user.id, httpSession.id, joinRoomJson.currentRoom)

    val userIds = userSessionsState.usersInRoom(joinRoomJson.currentRoom)
    val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
    val right = userRepository.getById(user.id).rights.find { it.roomId == joinRoomJson.currentRoom }!!
    return right.toRoomJson(roomUserJsons, roomRepository.getById(right.roomId).name)
  }
}

data class RoomJson(val id: UUID, val name: String, val write: Boolean = false, val users: List<RoomUserJson>)
data class RoomUserJson(val id: UUID, val username: String)
fun Right.toRoomJson(users: List<RoomUserJson>, roomName: String) = RoomJson(roomId, roomName, write, users)
fun User.toRoomUserJson() = RoomUserJson(id, username)
data class JoinRoomJson(val previousRoom: UUID?, val currentRoom: UUID)

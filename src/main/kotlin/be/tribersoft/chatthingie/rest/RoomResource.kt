package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.domain.Right
import be.tribersoft.chatthingie.domain.User
import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.state.UserSessionsState
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping(path = arrayOf("/rooms"))
class RoomResource(private val userRepository: UserRepository, private val userSessionsState: UserSessionsState) {

  @GetMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun mine(principal: Principal, session: HttpSession): List<RoomJson> {
    val user = (principal as UsernamePasswordAuthenticationToken).principal as User
    val rights = userRepository.getById(user.id).rights
    return rights.map {
      val userIds = userSessionsState.usersInRoom(it.room.id)
      val roomUserJsons = userRepository.getByIds(userIds).map { it.toRoomUserJson() }
      it.toRoomJson(roomUserJsons)
    }
  }
}

data class RoomJson(val id: UUID, val name: String, val write: Boolean = false, val users: List<RoomUserJson>)
data class RoomUserJson(val id: UUID, val username: String)
fun Right.toRoomJson(users: List<RoomUserJson>) = RoomJson(room.id, room.name, write, users)
fun User.toRoomUserJson() = RoomUserJson(id, username)

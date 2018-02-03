package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.domain.Right
import be.tribersoft.chatthingie.domain.User
import be.tribersoft.chatthingie.domain.UserRepository
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.servlet.http.HttpSession

@RestController
@RequestMapping(path = arrayOf("/rooms"))
class RoomResource(private val userRepository: UserRepository) {

  @GetMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun mine(principal: Principal, session: HttpSession): List<RoomJson> {
    val user = (principal as UsernamePasswordAuthenticationToken).principal as User
    return userRepository.getById(user.id).rights.map { it.toRoomJson() }
  }
}

data class RoomJson(val name: String, val write: Boolean = false)
fun Right.toRoomJson() = RoomJson(room, write)

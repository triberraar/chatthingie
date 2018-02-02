package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.domain.Right
import be.tribersoft.chatthingie.domain.User
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*

@RestController
@RequestMapping(path = arrayOf("/users"))
class UserResource {

  @GetMapping(path = arrayOf("/me"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun me(principal: Principal): UserJson {
    println(principal)
    val user = (principal as UsernamePasswordAuthenticationToken).principal as User
    return user.toJson()
  }
}

data class RightJson(val room: String, val write: Boolean = false)
data class UserJson(val id: UUID, val username: String, val rights: List<RightJson> = listOf(), val roles: List<String> = listOf())

fun User.toJson() = UserJson(id,
    username,
    rights.map { it.toJson() },
    roles.map { it })

fun Right.toJson() = RightJson(room, write)

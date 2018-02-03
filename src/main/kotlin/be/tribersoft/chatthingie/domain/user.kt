package be.tribersoft.chatthingie.domain

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

data class User(val id: UUID = UUID.randomUUID(), private val username: String, private val password: String, val rights: Set<Right>, val roles: Set<String>): UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.map { SecurityAuthority(it) }.toMutableSet()

    override fun isEnabled() = true

    override fun getUsername() = username

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = password

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}

data class SecurityAuthority(val role: String): GrantedAuthority {
    override fun getAuthority(): String {
        return role
    }
}

data class Room(val name: String, val id: UUID = UUID.randomUUID())

data class Right(val room: Room, val write: Boolean)

@Component
class UserRepository(private val users: Set<User> = initialUsers()) {

    fun getByUsernameAndPassword(username: String, password: String): User {
        val user = users.find { it.username == username && it.password == password }
        return if(user != null) user else throw BadCredentialsException("invalid login")
    }

    fun getById(id: UUID): User = users.find { it.id.equals(id) }!!
    fun getByIds(ids: List<UUID>) = users.filter { ids.contains(it.id) }

    fun loadUserByUsername(username: String): UserDetails {
        val user = users.find { it.username == username }
        return if(user != null) user else throw BadCredentialsException("invalid login")
    }

}

@Component
class RoomRepository() {
    fun all() = setOf("room1", "room2")
}

fun initialUsers(): Set<User> {
    return setOf<User>(User(
            username = "admin",
            password = "admin",
            rights = setOf(
              Right(room1, true),
              Right(room2, true),
              Right(room3, true)
            ),
            roles = setOf("ROLE_USER", "ROLE_ADMIN")
    ), User(
            username = "user1",
            password = "user1",
            rights = setOf(
                    Right(room1, true),
                    Right(room2, false)
            ),
            roles = setOf("ROLE_USER")
    ),  User(
            username = "user1",
            password = "user1",
            rights = setOf(
                    Right(room1, true),
                    Right(room2, false)
            ),
            roles = setOf("ROLE_USER"))
    )
}

val room1 = Room("room1")
val room2 = Room("room2")
val room3 = Room("room3")

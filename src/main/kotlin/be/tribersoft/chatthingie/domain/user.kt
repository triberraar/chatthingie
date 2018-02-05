package be.tribersoft.chatthingie.domain

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

data class User(val id: UUID = UUID.randomUUID(), private val username: String, private val password: String, private var enabled: Boolean, val rights: MutableSet<Right>, val roles: Set<String>) : UserDetails {
  override fun getAuthorities(): MutableCollection<out GrantedAuthority> = roles.map { SecurityAuthority(it) }.toMutableSet()

  override fun isEnabled() = enabled

  fun setEnabled(e: Boolean) {
    enabled = e
  }

  override fun getUsername() = username

  override fun isCredentialsNonExpired() = true

  override fun getPassword() = password

  override fun isAccountNonExpired() = true

  override fun isAccountNonLocked() = true
}

data class SecurityAuthority(val role: String) : GrantedAuthority {
  override fun getAuthority(): String {
    return role
  }
}

data class Right(val roomId: UUID, val write: Boolean)

@Component
class UserRepository(private val users: MutableSet<User> = initialUsers()) {

  fun getByUsernameAndPassword(username: String, password: String): User {
    val user = users.find { it.username == username && it.password == password && it.isEnabled }
    return if (user != null) user else throw BadCredentialsException("invalid login")
  }

  fun getById(id: UUID): User = users.find { it.id.equals(id) }!!

  fun getByIds(ids: List<UUID>) = users.filter { ids.contains(it.id) }

  fun loadUserByUsername(username: String): UserDetails {
    val user = users.find { it.username == username }
    return if (user != null) user else throw BadCredentialsException("invalid login")
  }

  fun all() = users

  fun add(user: User) = users.add(user)

  fun remove(userId: UUID) = users.removeIf { it.id == userId }

}

fun initialUsers(): MutableSet<User> {
  return mutableSetOf(User(
    username = "admin",
    password = "admin",
    rights = mutableSetOf(
      Right(room1.id, true),
      Right(room2.id, true),
      Right(room3.id, true)
    ),
    roles = setOf("ROLE_USER", "ROLE_ADMIN"),
    enabled = true
  ), User(
    username = "user1",
    password = "user1",
    rights = mutableSetOf(
      Right(room1.id, true),
      Right(room2.id, false)
    ),
    roles = setOf("ROLE_USER"),
    enabled = true
  ), User(
    username = "user2",
    password = "user2",
    rights = mutableSetOf(
      Right(room1.id, true),
      Right(room2.id, false)
    ),
    roles = setOf("ROLE_USER"),
    enabled = true
  )
  )
}

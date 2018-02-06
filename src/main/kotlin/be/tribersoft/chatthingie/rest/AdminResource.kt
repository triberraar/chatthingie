package be.tribersoft.chatthingie.rest

import be.tribersoft.chatthingie.domain.Right
import be.tribersoft.chatthingie.domain.Room
import be.tribersoft.chatthingie.domain.RoomRepository
import be.tribersoft.chatthingie.domain.UserRepository
import be.tribersoft.chatthingie.state.UserSessionsState
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = arrayOf("/admin/room"))
@PreAuthorize("hasRole('ROLE_ADMIN')")
class AdminRoomResource(private val roomRepository: RoomRepository, private val sessionsState: UserSessionsState, private val userRepository: UserRepository) {

  @GetMapping(path = arrayOf("all"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun all() = roomRepository.all().map { AdminRoomJson(it.name, it.withHistory, it.id) }

  @PostMapping( produces = arrayOf(MediaType.APPLICATION_JSON_VALUE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun create(@RequestBody adminRoomJson: AdminRoomJson): AdminRoomJson {
    val room = Room(adminRoomJson.name, adminRoomJson.withHistory)
    roomRepository.add(room)
    return AdminRoomJson(room.name, room.withHistory, room.id)
  }

  @DeleteMapping(path = arrayOf("/{roomId}"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun delete(@PathVariable("roomId") roomId: UUID) {
    if(sessionsState.usersInRoom(roomId).isNotEmpty()) {
      throw RuntimeException("Can't delete room when there are users in it")
    }
    userRepository.all().forEach { it.rights.removeIf { right -> right.roomId == roomId } }
    roomRepository.delete(roomId)
  }
}

data class AdminRoomJson(val name: String, val withHistory: Boolean, val id: UUID?)

@RestController
@RequestMapping(path = arrayOf("/admin/users"))
@PreAuthorize("hasRole('ROLE_ADMIN')")
class AdminUsersResource(private val userRepository: UserRepository,
                         private val roomRepository: RoomRepository,
                         private val userSessionsState: UserSessionsState) {

  @PutMapping(path = arrayOf("/{id}/activate"))
  fun activate(@PathVariable userId: UUID) {
    userRepository.getById(userId).isEnabled = true
  }

  @PutMapping(path = arrayOf("/{id}/deactivate"))
  fun deactivate(userId: UUID) {
    userRepository.getById(userId).isEnabled = false
    userSessionsState.deactivate(userId)

  }

  @PutMapping(path = arrayOf("/{id}/rooms"), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun setRooms(@PathVariable("id") userId: UUID, @RequestBody adminRightJsons: List<AdminRightJson>): List<AdminRightJson> {
    userRepository.getById(userId).rights.clear()
    userRepository.getById(userId).rights.addAll(adminRightJsons.map { Right(it.roomId, it.write) })
    return userRepository.getById(userId).rights.map { AdminRightJson(it.roomId, it.write, roomRepository.getById(roomId = it.roomId).name) }
  }

  @GetMapping(produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun all() = userRepository.all().map { AdminUserJson(it.id, it.isEnabled, it.username) }

  @GetMapping(path = arrayOf("/{id}"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
  fun get(@PathVariable userId: UUID): AdminUserDetailsJson {
    val user = userRepository.getById(userId)
    return AdminUserDetailsJson(user.id, user.isEnabled, user.username, user.rights.map { AdminRightJson(it.roomId, it.write, roomRepository.getById(it.roomId).name) })
  }
}

data class AdminRightJson(val roomId: UUID, val write: Boolean = false, val roomName: String?)
data class AdminUserJson(val userId: UUID, val enabled: Boolean, val username: String)
data class AdminUserDetailsJson(val userId: UUID, val enabled: Boolean, val username: String, val rights: List<AdminRightJson>)
